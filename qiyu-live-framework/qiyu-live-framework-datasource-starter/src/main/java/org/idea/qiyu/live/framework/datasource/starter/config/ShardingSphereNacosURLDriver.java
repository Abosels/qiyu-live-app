package org.idea.qiyu.live.framework.datasource.starter.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import io.micrometer.common.util.StringUtils;
import org.apache.shardingsphere.infra.url.spi.ShardingSphereURLLoader;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ShardingSphere 5.5.2 URL loader.
 * <p>
 * 这个实现让 ShardingSphere 能识别自定义的 nacos: 协议，
 * 然后从 Nacos 中读取真正的 qiyu-db-sharding.yaml 内容。
 */
public final class ShardingSphereNacosURLDriver implements ShardingSphereURLLoader {

    /**
     * 自定义协议前缀。
     */
    private static final String TYPE = "nacos:";

    /**
     * Nacos 默认分组。
     */
    private static final String GROUP_DEFAULT = "DEFAULT_GROUP";

    /**
     * Nacos 默认命名空间。
     */
    private static final String NAMESPACE_DEFAULT = "public";

    /**
     * Nacos 默认账号。
     */
    private static final String NACOS_USERNAME_DEFAULT = "nacos";
    private static final String NACOS_PASSWORD_DEFAULT = "nacos";

    /**
     * 只展开真正的环境变量占位符，避免误伤 ShardingSphere 的 inline 分片表达式。
     * 例如：${QIYU_MYSQL_MASTER_HOST:127.0.0.1}
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([A-Z0-9_]+)(?::([^}]*))?}");

    @Override
    public String load(String url, Properties properties) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("ShardingSphere nacos url must not be empty.");
        }

        // 最小日志：确认 SPI 真的被 ShardingSphere 调用了。
        System.out.println("[DEBUG-SPI] ShardingSphereNacosURLDriver.load invoked, url=" + url);

        NacosUrlModel nacosUrlModel = parseNacosUrl(url);

        String username = firstNonEmpty(nacosUrlModel.username,
                properties.getProperty(PropertyKeyConst.USERNAME),
                NACOS_USERNAME_DEFAULT);
        String password = firstNonEmpty(nacosUrlModel.password,
                properties.getProperty(PropertyKeyConst.PASSWORD),
                NACOS_PASSWORD_DEFAULT);
        String namespace = firstNonEmpty(nacosUrlModel.namespace,
                properties.getProperty(PropertyKeyConst.NAMESPACE),
                NAMESPACE_DEFAULT);
        String group = firstNonEmpty(nacosUrlModel.group,
                properties.getProperty("group"),
                GROUP_DEFAULT);

        System.out.println("[DEBUG-SPI] resolved nacos config -> serverAddr=" + nacosUrlModel.serverAddr
                + ", dataId=" + nacosUrlModel.dataId
                + ", group=" + group
                + ", namespace=" + namespace);

        Properties nacosProperties = new Properties();
        nacosProperties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosUrlModel.serverAddr);
        nacosProperties.setProperty(PropertyKeyConst.USERNAME, username);
        nacosProperties.setProperty(PropertyKeyConst.PASSWORD, password);
        nacosProperties.setProperty(PropertyKeyConst.NAMESPACE, namespace);

        try {
            ConfigService configService = NacosFactory.createConfigService(nacosProperties);
            String content = configService.getConfig(nacosUrlModel.dataId, group, 5000);
            if (StringUtils.isEmpty(content)) {
                throw new IllegalStateException("Nacos config is empty, dataId=" + nacosUrlModel.dataId
                        + ", group=" + group
                        + ", serverAddr=" + nacosUrlModel.serverAddr
                        + ", namespace=" + namespace);
            }

            // 先把 Nacos YAML 中的环境变量占位符展开，再交给 ShardingSphere。
            String resolvedContent = resolvePlaceholders(content);
            if (!content.equals(resolvedContent)) {
                System.out.println("[DEBUG-SPI] resolved placeholders in qiyu-db-sharding.yaml before handing to ShardingSphere.");
            }
            return resolvedContent;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load ShardingSphere config from Nacos, url=" + url, e);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * 解析自定义 Nacos URL。
     */
    private NacosUrlModel parseNacosUrl(String url) {
        String normalized = url.trim();

        // 兼容 jdbc:shardingsphere: 这样的外层前缀。
        int nacosIndex = normalized.indexOf("nacos");
        if (nacosIndex > 0) {
            normalized = normalized.substring(nacosIndex);
        }

        // 去掉 nacos: 或 nacos:// 前缀。
        if (normalized.startsWith("nacos://")) {
            normalized = normalized.substring("nacos://".length());
        } else if (normalized.startsWith("nacos:")) {
            normalized = normalized.substring("nacos:".length());
        }

        // 去掉可能残留的多余斜杠。
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        // 第一段是 serverAddr，第二段是 dataId + query。
        String[] addressAndPath = normalized.split("/", 2);
        if (addressAndPath.length != 2) {
            throw new IllegalArgumentException("Invalid nacos url, missing dataId path: " + url);
        }

        String serverAddr = addressAndPath[0];
        String pathAndQuery = addressAndPath[1];

        String[] dataIdAndQuery = pathAndQuery.split("\\?", 2);
        String dataId = dataIdAndQuery[0];
        String query = dataIdAndQuery.length > 1 ? dataIdAndQuery[1] : "";

        NacosUrlModel model = new NacosUrlModel();
        model.serverAddr = serverAddr;
        model.dataId = dataId;
        model.group = GROUP_DEFAULT;

        if (!StringUtils.isEmpty(query)) {
            String[] items = query.split("&");
            for (String item : items) {
                if (StringUtils.isEmpty(item)) {
                    continue;
                }
                String[] keyValue = item.split("=", 2);
                if (keyValue.length != 2) {
                    continue;
                }
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                if ("username".equalsIgnoreCase(key)) {
                    model.username = value;
                } else if ("password".equalsIgnoreCase(key)) {
                    model.password = value;
                } else if ("namespace".equalsIgnoreCase(key) || "nameSpace".equalsIgnoreCase(key)) {
                    model.namespace = value;
                } else if ("group".equalsIgnoreCase(key)) {
                    model.group = value;
                }
            }
        }

        return model;
    }

    /**
     * 按顺序返回第一个非空值。
     */
    private String firstNonEmpty(String... candidates) {
        for (String candidate : candidates) {
            if (!StringUtils.isEmpty(candidate)) {
                return candidate;
            }
        }
        return "";
    }

    /**
     * 展开形如 ${QIYU_MYSQL_MASTER_HOST:127.0.0.1} 的环境变量占位符。
     */
    private String resolvePlaceholders(String content) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String defaultValue = matcher.group(2) == null ? "" : matcher.group(2);
            String value = firstNonEmpty(System.getenv(key), System.getProperty(key), defaultValue);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 只保存解析后的 Nacos 访问参数，方便后续排查。
     */
    private static final class NacosUrlModel {
        private String serverAddr;
        private String dataId;
        private String group;
        private String username;
        private String password;
        private String namespace;
    }
}