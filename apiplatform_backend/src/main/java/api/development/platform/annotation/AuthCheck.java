package api.development.platform.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 */
@Target(ElementType.METHOD) // 自定义注解@AuthCheck，METHOD为自定义注解的作用域 用于方法上
@Retention(RetentionPolicy.RUNTIME) // 指定注解的保留策略，可以在运行时通过反射获取
public @interface AuthCheck {

    /**
     * 必须有某个角色
     * mustRole 通过注解决定赋值
     * @return
     */
    String mustRole() default "";

}

