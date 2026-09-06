package org.qiyu.live.user.provider.service;

public interface TagBitRegistryService {
    boolean isAllowed(String fieldName, int bitIndex);

    String getCode(String fieldName, int bitIndex);
}
