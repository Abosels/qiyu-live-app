package org.qiyu.live.user.provider.service.impl;

import jakarta.annotation.PostConstruct;
import org.qiyu.live.user.provider.service.TagBitRegistryService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class TagBitRegistryServiceImpl implements TagBitRegistryService {

    private static final Set<String> ALLOWED_STATUS = Set.of("ACTIVE", "DEPRECATED", "RESERVED");

    /**
     * 加载位定义注册表。
     * 启动时校验配置，发现问题直接失败，避免脏配置上线。
     */
    private Map<String, Object> root;

    @PostConstruct
    public void init() throws Exception {
        Yaml yaml = new Yaml();
        try (InputStream is = new ClassPathResource("tag-bit-registry.yaml").getInputStream()) {
            root = yaml.load(is);
        }
        validateRegistry();
    }

    /**
     * 判断某个 fieldName + bitIndex 是否允许使用。
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean isAllowed(String fieldName, int bitIndex) {
        if (bitIndex < 0 || bitIndex > 62) {
            return false;
        }
        if (root == null) {
            return false;
        }

        Map<String, Object> fields = (Map<String, Object>) root.get("fields");
        if (fields == null || !fields.containsKey(fieldName)) {
            return false;
        }

        Object fieldDefObj = fields.get(fieldName);
        if (!(fieldDefObj instanceof Map<?, ?> fieldDef)) {
            return false;
        }

        Object bitsObj = fieldDef.get("bits");
        if (!(bitsObj instanceof Map<?, ?> bits)) {
            return false;
        }

        Object metaObj = bits.get(String.valueOf(bitIndex));
        if (!(metaObj instanceof Map<?, ?> metaMap)) {
            return false;
        }

        String status = String.valueOf(metaMap.get("status"));
        return "ACTIVE".equalsIgnoreCase(status);
    }

    /**
     * 获取某个位对应的 code，用于同步维护 tagInfo 可读字段。
     */
    @Override
    @SuppressWarnings("unchecked")
    public String getCode(String fieldName, int bitIndex) {
        if (root == null || bitIndex < 0 || bitIndex > 62) {
            return null;
        }

        Map<String, Object> fields = (Map<String, Object>) root.get("fields");
        if (fields == null || !fields.containsKey(fieldName)) {
            return null;
        }

        Object fieldDefObj = fields.get(fieldName);
        if (!(fieldDefObj instanceof Map<?, ?> fieldDef)) {
            return null;
        }

        Object bitsObj = fieldDef.get("bits");
        if (!(bitsObj instanceof Map<?, ?> bits)) {
            return null;
        }

        Object metaObj = bits.get(String.valueOf(bitIndex));
        if (!(metaObj instanceof Map<?, ?> metaMap)) {
            return null;
        }

        Object code = metaMap.get("code");
        return code == null ? null : String.valueOf(code);
    }

    @SuppressWarnings("unchecked")
    private void validateRegistry() {
        if (root == null) {
            throw new IllegalStateException("tag-bit-registry.yaml is empty or failed to load");
        }

        Map<String, Object> fields = (Map<String, Object>) root.get("fields");
        if (fields == null || fields.isEmpty()) {
            throw new IllegalStateException("fields cannot be empty");
        }

        Set<String> globalCodes = new HashSet<>();

        for (Map.Entry<String, Object> fieldEntry : fields.entrySet()) {
            String fieldName = fieldEntry.getKey();
            if (!isValidFieldName(fieldName)) {
                throw new IllegalStateException("invalid fieldName: " + fieldName);
            }

            if (!(fieldEntry.getValue() instanceof Map<?, ?> fieldDef)) {
                throw new IllegalStateException("invalid field definition: " + fieldName);
            }

            Object bitsObj = fieldDef.get("bits");
            if (bitsObj == null) {
                continue;
            }
            if (!(bitsObj instanceof Map<?, ?> bits)) {
                throw new IllegalStateException("invalid bits definition: " + fieldName);
            }

            Set<Integer> bitIndexSet = new HashSet<>();
            for (Map.Entry<?, ?> bitEntry : bits.entrySet()) {
                String bitKey = String.valueOf(bitEntry.getKey());
                int bitIndex = parseBitIndex(fieldName, bitKey);
                if (!bitIndexSet.add(bitIndex)) {
                    throw new IllegalStateException("duplicated bit index: " + fieldName + "." + bitIndex);
                }

                if (!(bitEntry.getValue() instanceof Map<?, ?> meta)) {
                    throw new IllegalStateException("invalid bit meta: " + fieldName + "." + bitIndex);
                }

                String code = requireText(meta, "code", fieldName, bitIndex);
                String status = requireText(meta, "status", fieldName, bitIndex).toUpperCase(Locale.ROOT);

                if (!ALLOWED_STATUS.contains(status)) {
                    throw new IllegalStateException("invalid status: " + fieldName + "." + bitIndex + " -> " + status);
                }
                if (!globalCodes.add(code)) {
                    throw new IllegalStateException("duplicated code: " + code);
                }
            }
        }
    }

    /**
     * 只允许 tag_info1 / tag_info2 / tag_info3。
     */
    private boolean isValidFieldName(String fieldName) {
        return "tag_info1".equals(fieldName) || "tag_info2".equals(fieldName) || "tag_info3".equals(fieldName);
    }

    /**
     * bit 下标只能是 0~62。
     */
    private int parseBitIndex(String fieldName, String bitKey) {
        try {
            int idx = Integer.parseInt(bitKey);
            if (idx < 0 || idx > 62) {
                throw new IllegalStateException("bitIndex out of range: " + fieldName + "." + idx);
            }
            return idx;
        } catch (NumberFormatException e) {
            throw new IllegalStateException("bitIndex must be numeric: " + fieldName + "." + bitKey);
        }
    }

    /**
     * 读取并校验必填文本字段。
     */
    private String requireText(Map<?, ?> meta, String key, String fieldName, int bitIndex) {
        Object val = meta.get(key);
        if (val == null) {
            throw new IllegalStateException("missing key " + key + " at " + fieldName + "." + bitIndex);
        }
        String text = String.valueOf(val).trim();
        if (text.isEmpty()) {
            throw new IllegalStateException("empty key " + key + " at " + fieldName + "." + bitIndex);
        }
        return text;
    }
}
