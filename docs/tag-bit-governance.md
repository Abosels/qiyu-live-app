每个 fieldName(bitIndex) 只能分配给一个 code。
新增位前，必须先改 tag-bit-registry.yaml 再发版。
status 只允许:ACTIVE、DEPRECATED、RESERVED。
删除标签不删位，只改 DEPRECATED（避免历史数据解释错乱）。
禁止复用 DEPRECATED 位，除非做全量数据迁移并评审通过。