package api.development.platform.aop;

import api.development.apiplatform_interface.model.entity.User;
import api.development.platform.annotation.AuthCheck; // 自定义注解
import api.development.platform.common.ErrorCode;
import api.development.platform.exception.BusinessException;
import api.development.platform.model.enums.UserRoleEnum;
import api.development.platform.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 */
@Aspect // aop 切面类注解
@Component // 将当前类加载为bean，这样就可以使用依赖注入的方式使用
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     * @param joinPoint aop 中执行目标方法的对象
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)") //  @Around环绕通知，在方法执行前后添加处理
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 拿到权限字段
        String mustRole = authCheck.mustRole();
        // RequestContextHolder类，获取当前请求上下文信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // RequestAttributes 对象是一个更通用的请求属性持有者；现在需要拿到请求参数 请求信息就需要使用HttpServletRequest
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 如果当前拦截器指定的方法不需要权限，放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        // 必须有该权限才通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 如果被封号，直接拒绝
        if (UserRoleEnum.BAN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须有管理员权限
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum)) {
            // 用户没有管理员权限，拒绝
            if (!UserRoleEnum.ADMIN.equals(userRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

