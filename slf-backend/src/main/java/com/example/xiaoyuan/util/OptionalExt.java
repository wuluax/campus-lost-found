package com.example.xiaoyuan.util;

/**
 * 简单的工具类，提取文件扩展名。
 */
public class OptionalExt {
    public static String getFileExtension(String filename) {
        if (filename == null) return null;
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return null;
        return filename.substring(idx + 1);
    }
}
