package com.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @className:
 * @Name: luo chuan
 * @Date: 2018/6/10 19:44
 * @Description: 本地缓存
 */
public class TokenCache {

    // token前缀
    public static  final String TOKEN_PREFIX = "token_";

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    // LRU算法
    private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12,TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        @Override
        public String load(String s) throws Exception {
            return "null";
        }
    });

    public static void setKey(String key, String value) {
        loadingCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = loadingCache.get(key);
            if("null".equals(value)) {
                return null;
            }
            return  value;
        }
        catch (Exception e) {
            logger.error("localCache error", e);
        }
        return null;
    }
}
