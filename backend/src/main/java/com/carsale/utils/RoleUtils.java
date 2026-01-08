package com.carsale.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 角色权限工具类
 * 提供角色验证和权限检查功能
 */
public class RoleUtils {

    /**
     * 检查用户是否具有指定角色
     */
    public static boolean hasRole(String userRole, String requiredRole) {
        if (userRole == null || requiredRole == null) {
            return false;
        }
        return userRole.equalsIgnoreCase(requiredRole);
    }

    /**
     * 检查用户是否具有指定角色列表中的任何一个角色
     */
    public static boolean hasAnyRole(String userRole, String[] requiredRoles) {
        if (userRole == null || requiredRoles == null || requiredRoles.length == 0) {
            return false;
        }
        for (String role : requiredRoles) {
            if (userRole.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求中获取用户角色
     * 这里简化处理，实际应该从JWT token或session中获取
     */
    public static String getCurrentUserRole(HttpServletRequest request) {
        // 从请求头中获取角色信息（实际项目中应该从token中解析）
        String role = request.getHeader("X-User-Role");
        return role;
    }

    /**
     * 检查用户是否具有管理员权限
     */
    public static boolean isAdmin(String userRole) {
        return hasRole(userRole, "admin");
    }

    /**
     * 检查用户是否具有销售权限
     */
    public static boolean isSales(String userRole) {
        return hasRole(userRole, "sales");
    }

    /**
     * 检查用户是否具有仓库管理权限
     */
    public static boolean isWarehouse(String userRole) {
        return hasRole(userRole, "warehouse");
    }

    /**
     * 检查用户是否具有财务权限
     */
    public static boolean isFinance(String userRole) {
        return hasRole(userRole, "finance");
    }

    /**
     * 检查用户是否具有经理权限
     */
    public static boolean isManager(String userRole) {
        return hasRole(userRole, "manager");
    }

    /**
     * 获取角色的中文名称
     */
    public static String getRoleName(String role) {
        if (role == null) {
            return "未知角色";
        }
        switch (role.toLowerCase()) {
            case "admin":
                return "管理员";
            case "sales":
                return "销售员";
            case "warehouse":
                return "库管员";
            case "finance":
                return "财务员";
            case "manager":
                return "经理";
            default:
                return "未知角色";
        }
    }
}
