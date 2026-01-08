package com.carsale.aspect;

import com.carsale.annotation.RequiresRole;
import com.carsale.utils.RoleUtils;
import com.carsale.utils.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 角色权限检查切面
 * 用于处理带有@RequiresRole注解的方法的权限验证
 */
@Aspect
@Component
public class RoleCheckAspect {

    @Around("@annotation(com.carsale.annotation.RequiresRole) || @within(com.carsale.annotation.RequiresRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 获取用户角色（实际项目中应该从token中解析）
        String userRole = RoleUtils.getCurrentUserRole(request);

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresRole requiresRole = method.getAnnotation(RequiresRole.class);

        if (requiresRole == null) {
            // 检查类上的注解
            requiresRole = joinPoint.getTarget().getClass().getAnnotation(RequiresRole.class);
        }

        if (requiresRole != null && requiresRole.value().length > 0) {
            // 检查用户是否具有允许的角色
            if (!RoleUtils.hasAnyRole(userRole, requiresRole.value())) {
                String roleNames = String.join(", ", requiresRole.value());
                return Result.error(403, "您需要拥有以下角色之一才能访问此功能：" + roleNames);
            }
        }

        // 权限验证通过，继续执行方法
        return joinPoint.proceed();
    }
}
