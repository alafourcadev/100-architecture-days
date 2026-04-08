package com.architecturedays.day008.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * BEFORE: RedisCacheManager sin TTL, sin configuracion de serializacion.
 * Los datos se quedan en Redis para siempre. Datos fantasma garantizados.
 */
@Configuration
@Profile("before")
public class NaiveRedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // Sin TTL. Sin serializacion custom. Sin nada.
        return RedisCacheManager.builder(factory).build();
    }
}
