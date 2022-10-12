package com.foodwant.foodwant.config;

import com.foodwant.foodwant.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author JIA-YANG, LAI
 * @create 2022-10-07 下午 04:30
 */
@Slf4j
@Configuration
public class WebvcConfig extends WebMvcConfigurationSupport {
    @Override
    /**
     * 設置靜態資源映射
     * 由於沒有放在Static下，需要手動配置
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("設置靜態資源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 擴展mvc框架的消息轉換器
     * @param converters the list of configured converters to extend
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("增加消息轉換器");
       //建立消息轉換器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //設置對象轉換器，底層使用jakson將java轉json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //將上面的消息轉換器加到mvc的轉換器集合中
        converters.add(0,messageConverter);//0為優先使用
    }
}
