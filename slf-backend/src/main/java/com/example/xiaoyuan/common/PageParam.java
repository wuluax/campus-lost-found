package com.example.xiaoyuan.common;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 通用分页请求参数。
 */
@Data
public class PageParam {
    @Min(value = 1, message = "页码必须>=1")
    private int pageNo = 1;

    @Min(value = 1, message = "每页数量必须>=1")
    private int pageSize = 10;
}