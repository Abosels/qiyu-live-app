package org.qiyu.live.common.interfaces.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import static javax.crypto.Cipher.PUBLIC_KEY;

public class AESUtils {
    /**
     * ALGORITHM：告诉 Java 你用什么算法
     * TRANSFORMATION：告诉它用什么模式和填充方式
     * DEFAULT_KEY：你的固定密钥
     */
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    /**
     * AES key 长度必须是 16、24 或 32 字节。
     */
    private static final String DEFAULT_KEY = "bei1234567890123";
    /**
     * 1.判空
     * 2.拿到 Cipher
     * 3.初始化为加密模式
     * 4.明文转字节
     * 5.执行加密
     * 6.Base64 编码
     * 7.返回字符串
     * @param data
     * @return 加密后的数据
     */
    public static String encrypt(String data) {
        if (data == null || data.isBlank()) {
            return data;
        }
        try {
            //实例化 Cipher 对象,它用于完成实际的加密操作
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            //初始化 Cipher 对象，设置加密模式
            cipher.init(Cipher.ENCRYPT_MODE, buildKey());
            byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String result = Base64.getEncoder().encodeToString(bytes);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
        /**
         * 1.解密数据
         * 2.判空
         * 3.Base64 解码
         * 4.拿 Cipher
         * 5.初始化为解密模式
         * 6.执行解密
         * 7.转字符串
         * @param data
         * @return 解密后的数据
         */
        public static String decrypt (String data){
            if (data == null || data.isBlank()) {
                return data;
            }
            try {
                //实例化 Cipher 对象,它用于完成实际的加密操作
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                //初始化Cipher对象，设置为解密模式
                cipher.init(Cipher.DECRYPT_MODE, buildKey());
                byte[] bytes = Base64.getDecoder().decode(data);
                byte[] result = cipher.doFinal(bytes);
                return new String(result, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    private static SecretKeySpec buildKey () {
        return new SecretKeySpec(DEFAULT_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }
}
