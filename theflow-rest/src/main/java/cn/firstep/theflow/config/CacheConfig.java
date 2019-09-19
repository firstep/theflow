package cn.firstep.theflow.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alvin4u
 */
@EnableCaching
@Configuration
public class CacheConfig {

    @Value("#{${app.caches}}")
    private Map<String, String> specs;

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new ArrayList<>();
        for (Map.Entry<String, String> entry : specs.entrySet()) {
            caches.add(new CaffeineCache(entry.getKey(), Caffeine.from(entry.getValue()).build()));
        }
        cacheManager.setCaches(caches);

        return cacheManager;
    }
}
