package com.example.xiaoyuan.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * 阿里云 OSS 文件上传工具。
 * 负责生成对象键、上传二进制内容并返回公网访问地址。
 */
@Component
public class OssUtil {
    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.access-key-id}")
    private String accessKeyId;
    @Value("${oss.access-key-secret}")
    private String accessKeySecret;
    @Value("${oss.bucket}")
    private String bucket;
    @Value("${oss.public-url-prefix}")
    private String publicUrlPrefix;

    /**
     * 上传文件到 OSS 并返回可访问 URL。
     */
    public String upload(byte[] bytes, String folder, String originalFilename, String contentType) {
        String key = buildObjectKey(folder, originalFilename);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        if (StringUtils.hasText(contentType)) {
            metadata.setContentType(contentType);
        }

        OSS client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        try {
            client.putObject(bucket, key, input, metadata);
        } finally {
            try {
                input.close();
            } catch (Exception ignored) {
            }
            client.shutdown();
        }
        return buildPublicUrl(key);
    }

    /**
     * 构造对象存储 key：目录 + 随机文件名 + 原后缀。
     */
    private String buildObjectKey(String folder, String originalFilename) {
        String cleanFolder = StringUtils.hasText(folder) ? folder.replace("\\", "/") : "misc";
        if (cleanFolder.startsWith("/")) {
            cleanFolder = cleanFolder.substring(1);
        }
        if (cleanFolder.endsWith("/")) {
            cleanFolder = cleanFolder.substring(0, cleanFolder.length() - 1);
        }

        String ext = "";
        String filename = StringUtils.getFilename(originalFilename);
        String extension = StringUtils.getFilenameExtension(filename);
        if (StringUtils.hasText(extension)) {
            ext = "." + extension;
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        return cleanFolder + "/" + uuid + ext;
    }

    /**
     * 基于配置的公网前缀拼接完整访问地址。
     */
    private String buildPublicUrl(String key) {
        String prefix = publicUrlPrefix;
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        if (key.startsWith("/")) {
            return prefix + key;
        }
        return prefix + "/" + key;
    }
}
