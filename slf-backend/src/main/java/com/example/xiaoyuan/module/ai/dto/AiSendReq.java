package com.example.xiaoyuan.module.ai.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

//AI 发送消息请求参数。
@Data
public class AiSendReq {

    @Size(max = 300, message = "内容长度不能超过300字符")
    private String content;

    private Long itemId;

    @Size(max = 500, message = "图片地址长度不能超过500字符")
    private String imageUrl;
}
