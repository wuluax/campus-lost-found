package com.example.xiaoyuan.common;

/**
 * 名称-状态请求体抽象。
 * 主要用于分类、地点等通用“名称 + 启用状态”场景的参数校验与复用。
 */
public interface NameStatusReq {
    /**
     * 获取名称字段。
     */
    String getName();

    /**
     * 获取状态字段（0 禁用，1 启用）。
     */
    Integer getStatus();
}
