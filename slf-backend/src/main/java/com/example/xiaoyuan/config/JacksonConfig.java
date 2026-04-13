package com.example.xiaoyuan.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局配置：注册 JavaTimeModule，统一日期时间输出格式。
 */
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 通过全局时区确保时间输出一致
        mapper.setTimeZone(java.util.TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        return mapper;
    }
}