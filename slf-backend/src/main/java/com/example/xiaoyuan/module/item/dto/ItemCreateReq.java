package com.example.xiaoyuan.module.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发布物品请求。
 */
@Data
public class ItemCreateReq {
    /**
     * 物品类型：0 失物，1 招领。
     */
    @NotNull(message = "类型不能为空")
    private Integer type;

    /**
     * 物品描述信息。
     */
    @NotBlank(message = "描述不能为空")
    @Size(max = 300, message = "描述最长300字符")
    private String description;

    /**
     * 分类ID。
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 地点ID。
     */
    @NotNull(message = "地点不能为空")
    private Long locationId;

    /**
     * 物品发生时间（丢失/拾取时间）。
     */
    @NotNull(message = "时间不能为空")
    @Past(message = "时间必须早于当前时间")
    private LocalDateTime itemTime;

    /**
     * 联系方式。
     */
    @Size(max = 50, message = "联系方式最长50字符")
    private String contact;

    /**
     * 图片地址。
     */
    @Size(max = 500, message = "图片地址长度不能超过500字符")
    private String imageUrl;
}
