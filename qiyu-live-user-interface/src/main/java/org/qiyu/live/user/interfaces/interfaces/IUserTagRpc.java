package org.qiyu.live.user.interfaces.interfaces;

import org.qiyu.live.user.interfaces.constants.UserTagsEnum;

public interface IUserTagRpc {
    boolean setTag(Long userId,  UserTagsEnum tag);

    boolean cancelTag(Long userId,  UserTagsEnum tag);

    boolean containTag(Long userId,  UserTagsEnum tag);
    /**
     * 按位设置标签（通用接口，不依赖枚举）
     * @param userId 用户ID
     * @param fieldName 标签字段名：tag_info1 / tag_info2 / tag_info3
     * @param bitIndex 位下标（从0开始）
     * @param add true=置1，false=置0
     */
    boolean setTagByBit(Long userId, String fieldName, int bitIndex, boolean add);

    /**
     * 按位查询标签是否命中
     */
    boolean containTagByBit(Long userId, String fieldName, int bitIndex);
}
