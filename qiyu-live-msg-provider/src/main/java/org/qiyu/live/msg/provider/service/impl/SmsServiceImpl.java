package org.qiyu.live.msg.provider.service.impl;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.idea.qiyu.live.framework.redis.starter.key.MsgProviderCacheKeyBuilder;
import org.qiyu.live.msg.provider.config.ApplicationProperties;
import org.qiyu.live.msg.provider.config.SmsTemplateIDEnum;
import org.qiyu.live.msg.provider.config.ThreadPoolManager;
import org.qiyu.live.msg.provider.dao.mapper.SmsMapper;
import org.qiyu.live.msg.provider.dao.po.SmsPO;
import org.qiyu.live.msg.provider.service.ISmsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SmsServiceImpl implements ISmsService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private MsgProviderCacheKeyBuilder msgProviderCacheKeyBuilder;
    @Resource
    private SmsMapper smsMapper;
    @Resource
    private ApplicationProperties applicationProperties;

    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        if (phone == null){
            return MsgSendResultEnum.MSG_PARAM_ERROR;
        }
        //生成验证码  4位、6位,有效期(30、60s),同一个手机号不能重复发送验证码,为了避免这种情况 利用redis来存储验证码
        String codeCacheKey = msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        if (redisTemplate.hasKey(codeCacheKey)){
            log.info("手机号{}已发送验证码", phone);
            return MsgSendResultEnum.SEND_FAIL;
        }
        int code = RandomUtils.nextInt(100000, 999999);
        redisTemplate.opsForValue().set(codeCacheKey, code, 60, TimeUnit.SECONDS); // 60 seconds = 1 minute
        //异步发送验证码,并记入到发送记录中
        ThreadPoolManager.commonAsyncPool.execute(() -> {
            boolean sendResult = sendSmsToCCP(phone, code);
            if (sendResult){
                log.info("手机号{}发送验证码成功", phone);
                insertOne(phone, code);
            }
        });
        //插入验证码发送记录
        return MsgSendResultEnum.SEND_SUCCESS;
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        //参数校验 — 6 位验证码，范围 100000~999999
        if (StringUtils.isEmpty(phone) || code == null || code < 100000 || code > 999999){
            return new MsgCheckDTO(false,"参数错误");
        }
        //用phone，去redis查询验证码
        /**
         * Key 的生成规则是一致的: buildSmsLoginCodeKey(phone) 对同一个 phone 总是返回相同的 Key
         */
        String codeCacheKey = msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        Integer cacheKey = (Integer) redisTemplate.opsForValue().get(codeCacheKey);
        if (cacheKey == null || cacheKey < 100000 || cacheKey > 999999){
            return new MsgCheckDTO(false,"验证码已过期");
        }
        if (cacheKey.equals(code)){
            redisTemplate.delete(codeCacheKey);
            return new MsgCheckDTO(true,"验证码正确");
        }
        return new MsgCheckDTO(false,"验证码错误");
    }

    @Override
    public void insertOne(String phone, Integer code) {
        SmsPO smsPO = new SmsPO();
        smsPO.setPhone( phone);
        smsPO.setCode( code);
        smsMapper.insert(smsPO);// 使用 BaseMapper 提供的 insert 方法
    }

    /**
     * 模拟发送短信
     * @param phone
     * @param code
     */
    private boolean sendSmsToCCP(String phone, Integer code){
        try{
            //生产环境请求地址：app.cloopen.com
            String serverIp = applicationProperties.getSmsServerIp();
            //请求端口
            String serverPort = String.valueOf(applicationProperties.getServerPort());
            //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
            String accountSId = applicationProperties.getAccountSId();
            String accountToken = applicationProperties.getAccountToken();
            //请使用管理控制台中已创建应用的APPID
            String appId = applicationProperties.getAppId();
            CCPRestSmsSDK sdk = new CCPRestSmsSDK();
            sdk.init(serverIp, serverPort);
            sdk.setAccount(accountSId, accountToken);
            sdk.setAppId(appId);
            sdk.setBodyType(BodyType.Type_JSON);
            //测试账号，所有短信都会往这里发送
            String to = applicationProperties.getTestPhone();
            //免费开发测试使用的模板ID为1，具体内容：【云通讯】您的验证码是{1}，请于{2}分钟内正确输入。其中{1}和{2}为短信模板参数。
            String templateId= SmsTemplateIDEnum.SMS_LOGIN_CODE_TEMPLATE.getTemplateId();
            String[] datas = {String.valueOf(code),"1"};
            String subAppend="1234";  //可选 扩展码，四位数字 0~9999
            String reqId= UUID.randomUUID().toString();  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
            //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
            HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);
            log.info("phone is {}, code is {}", phone, code);
            if("000000".equals(result.get("statusCode"))){
                //正常返回输出data包体信息（map）
                HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
                Set<String> keySet = data.keySet();
                for(String key:keySet){
                    Object object = data.get(key);
                    System.out.println(key +" = "+object);
                    log.info("key is {}, object is {}",key,object);
                }
            }else{
                //异常返回输出错误码和错误信息
                log.error("错误码:{},错误信息:{}",result.get("statusCode"),result.get("msg"));
                return false;
            }
            return true;
        }catch (Exception e){
            log.error("[sendSmsToCCP] error is ", e);
            return false;
        }
    }

}
