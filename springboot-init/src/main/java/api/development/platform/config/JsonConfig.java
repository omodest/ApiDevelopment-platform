package api.development.platform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Spring MVC Json 配置
 */
@JsonComponent // 标识这是一个自定义（序列化或反序列化） Jackson组件；springboot会自动将该类注册到Jackson模块
public class JsonConfig {

    /**
     * 添加 Long 转 json 精度丢失的配置
     * ObjectMapper 用于处理Java对象与JSON数据之间的序列化和反序列化
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 使用jackson的构建器类 Jackson2ObjectMapperBuilder，构建一个ObjectMapper 对象
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 注册自定义序列化器和反序列化器的模块
        SimpleModule module = new SimpleModule();
        // 将Long类型的对象序列化为它们的字符串表示形式（包装类型）
        module.addSerializer(Long.class, ToStringSerializer.instance);
        // long类型的值序列化为它们的字符串表示形式（基本类型）
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // 注册到ObjectMapper 实例中，后续就可以使用自定义序列化和反序列化器
        objectMapper.registerModule(module);
        return objectMapper;
    }
}