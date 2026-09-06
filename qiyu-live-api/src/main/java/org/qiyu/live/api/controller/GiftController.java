package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.IGiftService;
import org.qiyu.live.api.vo.GiftConfigVO;
import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.api.vo.req.GiftReqVO;
import org.qiyu.live.web.starter.limit.RequestLimit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gift")
public class GiftController {

    @Resource
    private IGiftService giftService;

    /**
     * 获取礼物列表
     *
     * @return
     */
    @RequestLimit(limit = 10, second = 10, msg = "请求过于频繁，请稍后再试")
    @PostMapping("/listGift")
    public WebResponseVO listGift(){
        //调用Rpc的方法，检索出来礼物配置列表
        List<GiftConfigVO> giftConfigVOS = giftService.listGift();
        return WebResponseVO.success(giftConfigVOS);
    }

    /**
     * 发送礼物方式
     * 具体实现之后具体实现
     */
    //送礼 — 严格限流，防止刷礼物接口被滥用
    @RequestLimit(limit = 3, second = 10, msg = "送礼过于频繁，请稍后再试")
    @PostMapping("/sendGift")
    public WebResponseVO sendGift(@RequestBody GiftReqVO giftReqVO){
        return WebResponseVO.success(giftService.sendGift(giftReqVO));
    }
}
