package com.example.xiaoyuan.module.upload.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.util.OssUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传接口：提供物品图片、AI 图片、头像上传。
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    /**
     * 单文件最大上传大小（2MB）。
     */
    private static final long MAX_SIZE = 2 * 1024 * 1024;

    /**
     * OSS 上传工具。
     */
    private final OssUtil ossUtil;

    public UploadController(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    /**
     * 物品图片上传接口。
     */
    @PostMapping(value = "/item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResp<String> uploadItem(@RequestParam("file") MultipartFile file) {
        return ApiResp.ok(handleUpload(file, "item"));
    }

    /**
     * AI 图片上传接口。
     */
    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResp<String> uploadAi(@RequestParam("file") MultipartFile file) {
        return ApiResp.ok(handleUpload(file, "ai"));
    }

    /**
     * 头像上传接口。
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResp<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResp.ok(handleUpload(file, "avatar"));
    }

    /**
     * 统一上传处理：校验文件大小并上传到指定 OSS 目录。
     */
    private String handleUpload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "请上传文件");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "图片大小不能超过2MB");
        }
        try {
            return ossUtil.upload(file.getBytes(), folder, file.getOriginalFilename(), file.getContentType());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "上传失败");
        }
    }
}
