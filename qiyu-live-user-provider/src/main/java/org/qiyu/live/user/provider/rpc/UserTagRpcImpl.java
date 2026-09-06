package org.qiyu.live.user.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.user.interfaces.constants.UserTagsEnum;
import org.qiyu.live.user.interfaces.interfaces.IUserTagRpc;
import org.qiyu.live.user.provider.service.IUserTagService;

@DubboService
public class UserTagRpcImpl implements IUserTagRpc {

    @Resource
    private IUserTagService userTagService;

    @Override
    public boolean setTag(Long userId, UserTagsEnum tag) {
        return userTagService.setTag(userId, tag);
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum tag) {
        return userTagService.cancelTag(userId, tag);
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum tag) {
        return userTagService.containTag(userId, tag);
    }

    @Override
    public boolean setTagByBit(Long userId, String fieldName, int bitIndex, boolean add) {
        return userTagService.setTagByBit(userId, fieldName, bitIndex, add);
    }

    @Override
    public boolean containTagByBit(Long userId, String fieldName, int bitIndex) {
        return userTagService.containTagByBit(userId, fieldName, bitIndex);
    }

}