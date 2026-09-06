package org.qiyu.live.user.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.utils.AESUtils;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.id.generate.interfaces.enums.IdTypeEnum;
import org.qiyu.live.id.generate.interfaces.interfaces.IdGenerateRpc;
import org.qiyu.live.user.interfaces.dto.UserDTO;
import org.qiyu.live.user.interfaces.dto.UserLoginDTO;
import org.qiyu.live.user.interfaces.dto.UserPhoneDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserPhoneMapper;
import org.qiyu.live.user.provider.dao.po.UserPhonePO;
import org.qiyu.live.user.provider.service.IUserPhoneService;
import org.qiyu.live.user.provider.service.IUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserPhoneServiceImpl implements IUserPhoneService {

    @Resource
    private IUserPhoneMapper userPhoneMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private IUserService userService;
    @DubboReference
    private IdGenerateRpc idGenerateRpc;

    @Override
    public UserLoginDTO initLogin(String phone) {
        //phone 不能为空
        if(StringUtils.isEmpty( phone)){
            return null;
        }
        //是否注册过，如果注册过，则返回userId
        UserPhoneDTO userPhoneDTO = queryByPhone( phone);
        if (userPhoneDTO != null){
            return UserLoginDTO.loginSuccess(userPhoneDTO.getUserId(), userPhoneDTO.getPhone(), createAndLoginToken(userPhoneDTO.getUserId()));
        }
        //如果没注册过，生成user信息，插入手机记录，绑定userId
        return registerAndLogin(phone);

    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        if(StringUtils.isEmpty( phone)){
            return null;
        }
        //走缓存 先去redis检索
        String redisKey = cacheKeyBuilder.buildUserPhoneObjKey(phone);
        UserPhoneDTO userPhoneDTO = (UserPhoneDTO) redisTemplate.opsForValue().get(redisKey);
        if (userPhoneDTO != null){
            //属于空值返回对象
            if (userPhoneDTO.getUserId() == null){
                return null;
            }
            return userPhoneDTO;
        }
        //redis未命中，从数据库查询
        userPhoneDTO = this.queryByPhoneFromDB(phone);
        if(userPhoneDTO != null){
            //先插入缓存，再返回
            redisTemplate.opsForValue().set(redisKey,userPhoneDTO,30, TimeUnit.MINUTES);
            return userPhoneDTO;
        }
        //缓存击穿，redis和DB都未查到，空值缓存
        userPhoneDTO = new UserPhoneDTO();
        redisTemplate.opsForValue().set(redisKey,userPhoneDTO,5, TimeUnit.MINUTES);
        return null;
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        if(userId == null || userId < 100000){
            return Collections.emptyList();
        }
        String redisKey = cacheKeyBuilder.buildUserPhoneListKey(userId);
        List<Object> userPhoneList = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (!CollectionUtils.isEmpty(userPhoneList)){
            if (((UserPhoneDTO)userPhoneList.get(0)).getUserId() != null){
                return userPhoneList.stream().map(x -> (UserPhoneDTO)x).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
        List<UserPhoneDTO> userPhoneDTOS = this.queryByUserIdFromDB(userId);
        if(!CollectionUtils.isEmpty(userPhoneDTOS)){
            redisTemplate.opsForList().leftPushAll(redisKey,userPhoneDTOS);
            redisTemplate.expire(redisKey,30, TimeUnit.MINUTES);
            return userPhoneDTOS;
        }
        userPhoneDTOS = Arrays.asList(new UserPhoneDTO());
        redisTemplate.opsForList().leftPushAll(redisKey,userPhoneDTOS);
        redisTemplate.expire(redisKey,30, TimeUnit.MINUTES);
        return null;
    }

    /**
     * 创建并记录token
     * @param userId
     * @return token
     */

    public String createAndLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        //token -> xxx:xxx:token redis get userId
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildUserLoginTokenKey(token),token,30, TimeUnit.DAYS);
        return token;
    }

    public UserLoginDTO registerAndLogin(String phone) {
        Long userId = idGenerateRpc.getUnSeqId(IdTypeEnum.USER_ID.getCode());
        // 保护逻辑：如果 ID 生成失败，直接抛异常让调用方感知，不静默入库脏数据
        if (userId == null) {
            log.error("idGenerateRpc.getUnSeqId 返回 null，phone={}", phone);
            throw new RuntimeException("用户ID生成失败，请稍后重试");
        }
        log.info("新用户注册，phone={}, userId={}", phone, userId);
        UserDTO userDTO = new UserDTO();
        userDTO.setNickName("本站用户-" + userId);
        userDTO.setUserId(userId);
        userService.insertOne(userDTO);
        UserPhonePO userPhonePO = new UserPhonePO();
        userPhonePO.setUserId(userId);
        //加密手机号
        String encryptPhone = AESUtils.encrypt(phone);
        userPhonePO.setPhone(encryptPhone);
        userPhonePO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        userPhoneMapper.insert(userPhonePO);
        return UserLoginDTO.loginSuccess(userId, userPhonePO.getPhone(), createAndLoginToken(userId));
    }

    private UserPhoneDTO queryByPhoneFromDB(String phone) {
        LambdaQueryWrapper<UserPhonePO> queryWrapper = new LambdaQueryWrapper<>();
        //先加密在查库
        String encryptPhone = AESUtils.encrypt(phone);
        queryWrapper.eq(UserPhonePO::getPhone, encryptPhone);
        queryWrapper.eq(UserPhonePO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return ConvertBeanUtils.convert(userPhoneMapper.selectOne(queryWrapper), UserPhoneDTO.class);
    }

    /**
     *  从数据库里查找
     * @param userId
     * @return
     */
    private List<UserPhoneDTO> queryByUserIdFromDB(Long userId) {
        LambdaQueryWrapper<UserPhonePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPhonePO::getUserId, userId);
        queryWrapper.eq(UserPhonePO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return ConvertBeanUtils.convertList(userPhoneMapper.selectList(queryWrapper), UserPhoneDTO.class);
    }
}
