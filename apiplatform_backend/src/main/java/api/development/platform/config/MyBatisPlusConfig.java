package api.development.platform.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置
 */
@Configuration // 使用@Configuration注解的类时，Spring Boot会处理这些配置类，并根据它们来创建和管理Bean。
@MapperScan("api.development.platform.mapper")
public class MyBatisPlusConfig {

    /**
     * 拦截器配置
     * @return MybatisPlusInterceptor:是Mybatis-Plus框架中的一个拦截器，它可以用来增强Mybatis的功能，例如添加分页、乐观锁、逻辑删除等功能。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}