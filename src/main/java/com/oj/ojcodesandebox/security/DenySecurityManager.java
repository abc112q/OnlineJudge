package com.oj.ojcodesandebox.security;

import java.security.Permission;

/**
 * @author Ariel
 * 禁用所有权限的安全管理器
 */
public class DenySecurityManager extends SecurityManager{
    @Override
    public void checkPermission(Permission perm) {
        throw new SecurityException("无权限 "+ perm.getActions());
    }
}
