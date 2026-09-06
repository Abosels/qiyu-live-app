package org.qiyu.live.gift.provider.service.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class GiftCacheRemoveBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 需要删除缓存的礼物 ID。 */
    private Integer giftId;
}
