package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.IImService;
import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.api.vo.resp.ImConfigVO;
import org.qiyu.live.web.starter.limit.RequestLimit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/im")
public class ImController {

    @Resource
    private IImService imService;

    @RequestLimit(limit = 3, second = 10, msg = "请求过于频繁，请稍后再试")
    @PostMapping("/getImConfig")
    public WebResponseVO getImConfig(){
        ImConfigVO imConfigVO = imService.getImConfig();
        return WebResponseVO.success(imConfigVO);
    }
}
