package org.qiyu.live.user.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.user.interfaces.constants.UserTagsEnum;
import org.qiyu.live.user.provider.dao.po.UserTagMongoPO;
import org.qiyu.live.user.provider.service.IUserTagService;
import org.qiyu.live.user.provider.service.TagBitRegistryService;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class UserTagServiceImpl implements IUserTagService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private TagBitRegistryService tagBitRegistryService;

    /**
     * 给用户打标签。
     * 实际上是对对应 bit 位做置位，同时同步更新 tagInfo 可读字段。
     */
    @Override
    public boolean setTag(Long userId, UserTagsEnum tag) {
        if (!validParam(userId, tag)) {
            return false;
        }
        return updateTagAtomic(userId, tag.getFieldName(), tag.getTag(), enumToReadableKey(tag.name()), true);
    }

    /**
     * 取消用户标签。
     * 实际上是对对应 bit 位做清位，同时同步更新 tagInfo 可读字段。
     */
    @Override
    public boolean cancelTag(Long userId, UserTagsEnum tag) {
        if (!validParam(userId, tag)) {
            return false;
        }
        return updateTagAtomic(userId, tag.getFieldName(), tag.getTag(), enumToReadableKey(tag.name()), false);
    }

    /**
     * 判断用户是否包含某个标签。
     */
    @Override
    public boolean containTag(Long userId, UserTagsEnum tag) {
        if (!validParam(userId, tag)) {
            return false;
        }
        UserTagMongoPO record = queryByUserId(userId);
        if (record == null) {
            return false;
        }
        long tagInfoValue = getTagInfoValue(record, tag.getFieldName());
        return (tagInfoValue & tag.getTag()) == tag.getTag();
    }

    /**
     * 按位设置标签。
     * 这里会同时更新 bit 字段和 tagInfo 可读字段，保证两份数据一致。
     */
    @Override
    public boolean setTagByBit(Long userId, String fieldName, int bitIndex, boolean add) {
        if (!validBitParam(userId, fieldName, bitIndex)) {
            return false;
        }
        if (!tagBitRegistryService.isAllowed(fieldName, bitIndex)) {
            return false;
        }

        String code = tagBitRegistryService.getCode(fieldName, bitIndex);
        if (code == null || code.isBlank()) {
            return false;
        }
        long bitMask = 1L << bitIndex;
        return updateTagAtomic(userId, fieldName, bitMask, enumToReadableKey(code), add);
    }

    /**
     * 按位查询标签是否命中。
     */
    @Override
    public boolean containTagByBit(Long userId, String fieldName, int bitIndex) {
        if (!validBitParam(userId, fieldName, bitIndex)) {
            return false;
        }
        UserTagMongoPO record = queryByUserId(userId);
        if (record == null) {
            return false;
        }

        long value = getTagInfoValue(record, fieldName);
        long bitMask = 1L << bitIndex;
        return (value & bitMask) == bitMask;
    }

    /**
     * 原子更新 Mongo 文档。
     * bit 字段和 tagInfo 字段一起更新，避免并发下脏写。
     */
    private boolean updateTagAtomic(Long userId, String fieldName, long bitMask, String readableKey, boolean add) {
        long now = System.currentTimeMillis();

        Query query = new Query(Criteria.where("userId").is(userId));
        Update update = new Update()
                .set("updateTime", now)
                .set("tagInfo." + readableKey, add ? 1 : 0)
                .setOnInsert("userId", userId)
                .setOnInsert("createTime", now)
                .setOnInsert("tag_info1", 0L)
                .setOnInsert("tag_info2", 0L)
                .setOnInsert("tag_info3", 0L);

        if (add) {
            update.bitwise(fieldName).or(bitMask);
        } else {
            update.bitwise(fieldName).and(~bitMask);
        }

        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(true).returnNew(true);
        return mongoTemplate.findAndModify(query, update, options, UserTagMongoPO.class) != null;
    }

    /**
     * 查询用户标签文档。
     */
    private UserTagMongoPO queryByUserId(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.findOne(query, UserTagMongoPO.class);
    }

    /**
     * 读取对应的 bit 位整数值。
     */
    private long getTagInfoValue(UserTagMongoPO po, String fieldName) {
        if ("tag_info1".equals(fieldName)) {
            return po.getTagInfo1() == null ? 0L : po.getTagInfo1();
        }
        if ("tag_info2".equals(fieldName)) {
            return po.getTagInfo2() == null ? 0L : po.getTagInfo2();
        }
        if ("tag_info3".equals(fieldName)) {
            return po.getTagInfo3() == null ? 0L : po.getTagInfo3();
        }
        return 0L;
    }

    /**
     * 校验枚举参数。
     */
    private boolean validParam(Long userId, UserTagsEnum tag) {
        return userId != null && userId > 0 && tag != null;
    }

    /**
     * 校验按位参数。
     */
    private boolean validBitParam(Long userId, String fieldName, int bitIndex) {
        if (userId == null || userId <= 0) {
            return false;
        }
        if (!"tag_info1".equals(fieldName) && !"tag_info2".equals(fieldName) && !"tag_info3".equals(fieldName)) {
            return false;
        }
        return bitIndex >= 0 && bitIndex <= 62;
    }

    /**
     * 把枚举名或 code 转成 tagInfo 的可读 key。
     * 例如 IS_VIP -> isVip。
     */
    private String enumToReadableKey(String source) {
        String[] parts = source.toLowerCase().split("_");
        if (parts.length == 0) {
            return source;
        }
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
            if (parts[i].length() > 1) {
                sb.append(parts[i].substring(1));
            }
        }
        return sb.toString();
    }
}
