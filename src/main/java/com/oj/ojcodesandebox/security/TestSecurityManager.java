package com.oj.ojcodesandebox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Ariel
 */
public class TestSecurityManager {
    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());
        // 测试写文件的权限的时候先放行读
        FileUtil.writeString("aaa","bb", Charset.defaultCharset());
    }
}
