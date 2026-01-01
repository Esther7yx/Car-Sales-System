package com.carsale.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // 【新增】引入Java 8时间模块
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Web配置类，用于解决JSON中文编码问题
 * 确保API返回的JSON数据中中文字符正确显示
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置字符串消息转换器，使用UTF-8编码
     * 解决返回字符串时的中文乱码问题
     */
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    /**
     * 配置Jackson消息转换器，确保JSON序列化使用UTF-8编码
     * 解决JSON返回中的中文乱码问题，并修复时间序列化错误
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setDefaultCharset(StandardCharsets.UTF_8);

        // 配置ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // 【新增】注册JavaTimeModule，解决LocalDateTime序列化报错问题
        objectMapper.registerModule(new JavaTimeModule());

        // 禁用日期转时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 禁用空对象异常
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        converter.setObjectMapper(objectMapper);
        return converter;
    }

    /**
     * 配置消息转换器列表
     * 确保UTF-8编码的消息转换器优先使用
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 添加字符串转换器
        converters.add(stringHttpMessageConverter());
        // 添加JSON转换器
        converters.add(mappingJackson2HttpMessageConverter());
    }
}