package api.development.platform.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 * 配置 Redisson 客户端的，是创建和配置 RedissonClient 实例，以便在应用程序中使用 Redisson 进行 Redis 数据库的操作。
 *
 * Redisson 是一个基于 Redis 的 Java 驻内存数据网格  和分布式数据结构服务。
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis") // 读取配置文件中配置
@Data
public class RedissonConfig {

    private String host;

    private String port;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);
        // 2. 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}