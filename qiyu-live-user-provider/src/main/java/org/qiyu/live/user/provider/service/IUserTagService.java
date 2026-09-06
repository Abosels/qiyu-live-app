package org.qiyu.live.user.provider.service;

import org.qiyu.live.user.interfaces.constants.UserTagsEnum;

public interface IUserTagService {

    boolean setTag(Long userId, UserTagsEnum tag);

    boolean cancelTag(Long userId, UserTagsEnum tag);

    boolean containTag(Long userId, UserTagsEnum tag);

    boolean setTagByBit(Long userId, String fieldName, int bitIndex, boolean add);

    boolean containTagByBit(Long userId, String fieldName, int bitIndex);
}