package com.example.xiaoyuan.module.clue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 线索创建请求参数。
 */
@Data
public class ClueCreateReq {
    /**
     * 线索内容。
     */
    @NotBlank(message = "线索内容不能为空")
    @Size(max = 300, message = "线索内容长度不能超过300")
    private String content;
}
