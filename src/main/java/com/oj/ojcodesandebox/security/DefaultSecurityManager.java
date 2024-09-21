package com.oj.ojcodesandebox.security;

import java.security.Permission;

/**
 * @author Ariel
 */
public class DefaultSecurityManager extends SecurityManager {

    @Override
    public void checkPermission(Permission perm) {
        System.out.println("无限制");
        // super.checkPermission(perm);   这里只要开启了权限管理器默认就是禁用所有权限
    }
}
