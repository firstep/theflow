package cn.firstep.theflow.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Token Cache.
 * Base on SpringCache.
 *
 * @author Alvin4u
 */
@CacheConfig(cacheNames = "token")
@Component("cache-token")
public class TokenCache implements Cache<String, String> {

    @Cacheable(key = "#key")
    @Override
    public String get(String key) {
        return null;
    }

    @CachePut(key = "#key")
    @Override
    public String put(String key, String value, long ttl) {
        return value;
    }

    @CacheEvict(key = "#key")
    @Override
    public void remove(String key) {
    }

}
