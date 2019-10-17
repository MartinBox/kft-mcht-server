
package com.mcoder.kft.convert;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.io.IOException;

/**
 * http 请求与响应打印接口日志，输出是@ResposeBody String类型时
 *
 * @author: weiyuanhua
 * @since: 2017-03-10
 */
public class LogStringHttpMessageConverter extends StringHttpMessageConverter {


    @Override
    protected String readInternal(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException {
        return super.readInternal(clazz, inputMessage);
    }

    @Override
    protected void writeInternal(String s, HttpOutputMessage outputMessage) throws IOException {
        super.writeInternal(s, outputMessage);
    }

}
