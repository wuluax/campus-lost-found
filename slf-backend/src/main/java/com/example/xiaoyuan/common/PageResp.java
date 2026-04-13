package com.example.xiaoyuan.common;

import lombok.Data;

import java.util.List;

/**
 * 通用分页响应包装。
 */
@Data
public class PageResp<T> {
    private long total;
    private List<T> list;

    public static <T> PageResp<T> of(long total, List<T> list) {
        PageResp<T> p = new PageResp<>();
        p.total = total;
        p.list = list;
        return p;
    }
}