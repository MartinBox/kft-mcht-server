
package com.mcoder.kft.config;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.mcoder.kft.convert.LogJsonHttpMessageConverter;
import com.mcoder.kft.web.JsonPropertyFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2018/7/31 15:28
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    private static final List<MediaType> SUPPORTED_MEDIA_TYPES = Arrays.asList(MediaType.ALL);

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        //fastjson
        LogJsonHttpMessageConverter fastConveter = new LogJsonHttpMessageConverter();
        fastConveter.setSupportedMediaTypes(SUPPORTED_MEDIA_TYPES);
        fastConveter.setInFilters(new SerializeFilter[]{new JsonPropertyFilter()});
        fastConveter.setOutFilters(new SerializeFilter[]{new JsonPropertyFilter()});

        //string
        HttpMessageConverter<?> stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converters.add(stringConverter);
        converters.add(fastConveter);

    }

    /**
     * 忽略请求头中的Content-Type类型，默认接收所有类型
     *
     * @param configurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        super.configureContentNegotiation(configurer);
        configurer.ignoreAcceptHeader(true);
    }
}
