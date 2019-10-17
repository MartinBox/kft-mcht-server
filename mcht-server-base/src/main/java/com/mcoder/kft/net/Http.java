package com.mcoder.kft.net;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 1194688733@qq.com
 * @Description: http client
 * @date 2018/7/19 17:11
 */
public class Http {

    protected static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;
    protected static final int DEFAULT_TIMEOUT = 60;
    protected static final String DEFAULT_CHARSET = "utf-8";
    protected static final String DEFAULT_METHOD = HttpMethod.GET;
    protected static final RequestBody DEFAULT_BODY = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), "");
    private static final Logger logger = LoggerFactory.getLogger(Http.class);

    protected Request.Builder builder;

    protected StringBuilder urlParams;

    protected RequestBody body;

    protected String url;

    protected String method;

    public Http() {
        this(DEFAULT_TIMEUNIT, DEFAULT_TIMEOUT);
    }

    public Http(TimeUnit timeUnit, int timeout) {
        timeout(timeUnit, timeout);
        builder = new Request.Builder();
        method = DEFAULT_METHOD;
        body = DEFAULT_BODY;
    }

    public static Http anInstance() {
        return new Http();
    }

    public static Http anInstance(TimeUnit timeUnit, int timeout) {
        return new Http(timeUnit, timeout);
    }

    public Http timeout(TimeUnit timeUnit, int timeout) {

        return this;
    }

    public Http header(String key, String value) {
        builder.header(key, value);
        return this;
    }

    public Http header(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            headers.entrySet().forEach(e -> builder.header(e.getKey(), e.getValue()));
        }
        return this;
    }

    public Http urlParams(Map<String, String> urlParamMap) throws UnsupportedEncodingException {
        return urlParams(urlParamMap, DEFAULT_CHARSET, true);
    }

    public Http urlParams(Map<String, String> urlParamMap, String charset) throws UnsupportedEncodingException {
        return urlParams(urlParamMap, charset, true);
    }

    public Http urlParams(Map<String, String> urlParamMap, boolean isEncoding) throws UnsupportedEncodingException {
        return urlParams(urlParamMap, DEFAULT_CHARSET, isEncoding);
    }

    public Http urlParams(Map<String, String> urlParamMap, String charset, boolean isEncoding) throws UnsupportedEncodingException {
        if (urlParamMap != null && !urlParamMap.isEmpty()) {
            urlParams = new StringBuilder();
            for (Map.Entry<String, String> e : urlParamMap.entrySet()) {
                urlParams.append(String.format("%s=%s&", e.getKey(), isEncoding ? URLEncoder.encode(e.getValue(), charset) : e.getValue()));
            }
        }
        return this;
    }

    public Http url(String url) {
        this.url = url;
        return this;
    }

    public Http xmlBody(String text) {
        return body(MediaType.parse("application/xml; charset=utf-8"), text);
    }

    public Http jacksonBody(String text) {
        return body(MediaType.parse("application/json; charset=utf-8"), text);
    }

    public Http formBody(Map<String, String> formMap) {
        if (formMap != null && !formMap.isEmpty()) {
            FormBody.Builder formEncodingBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : formMap.entrySet()) {
                formEncodingBuilder.add(entry.getKey(), StringUtils.isNotBlank(entry.getValue()) ? entry.getValue() : "");
            }
            this.body = formEncodingBuilder.build();
        }
        return this;
    }

    public Http formBodyWithFile(Map<String, String> formMap, byte[] file, String fileName, String mapName) {
        if (formMap != null && !formMap.isEmpty()) {
            MultipartBody.Builder formEncodingBuilder = new MultipartBody.Builder();
            formEncodingBuilder.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> entry : formMap.entrySet()) {
                formEncodingBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            formEncodingBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + mapName + "\";filename=\"" + fileName + "\"", "Content-Transfer-Encoding", "binary"),
                    RequestBody.create(MediaType.parse("application/octet-stream"), file));
            this.body = formEncodingBuilder.build();
        }
        return this;
    }

    public Http body(MediaType type, String text) {
        this.body = RequestBody.create(type, text);
        return this;
    }

    public Http post() {
        this.method = HttpMethod.POST;
        return this;
    }

    public Http put() {
        this.method = HttpMethod.PUT;
        return this;
    }

    public Http get() {
        this.method = HttpMethod.GET;
        return this;
    }

    private void prepare() {
        if (StringUtils.isNotBlank(urlParams)) {
            url = String.format("%s?%s", url, urlParams.toString().replaceAll("&$", ""));
        }
        /*builder.url(url).method(method, method.equals(HttpMethod.POST) ? body : null);*/
        builder.url(url).method(method, okhttp3.internal.http.HttpMethod.requiresRequestBody(method) ? body : null);
    }

    public String execute() throws IOException {
        prepare();
        Response response = HttpClientHolder.getInstance().newCall(builder.build()).execute();
        if (null == response) {
            throw new HttpResponseException(500, "远程请求无应答");
        }
        if (response.isSuccessful()) {
            return response.body().string();
        }
        logger.info("request is failure, code:{} message:{}", response.code(), response.message());
        throw new HttpResponseException(response.code(), response.message());
    }

    public byte[] executeByte() throws IOException {
        prepare();
        Response response = HttpClientHolder.getInstance().newCall(builder.build()).execute();
        if (null == response) {
            throw new HttpResponseException(500, "远程请求无应答");
        }
        if (response.isSuccessful()) {
            return response.body().bytes();
        }
        logger.info("request is failure, code:{} message:{}", response.code(), response.message());
        throw new HttpResponseException(response.code(), response.message());
    }

    public InputStream executeStream() throws IOException {
        prepare();
        Response response = HttpClientHolder.getInstance().newCall(builder.build()).execute();
        if (response != null) {
            if (response.isSuccessful()) {
                return response.body().byteStream();
            } else {
                logger.info("request is failure, code:{} message:{}", response.code(), response.message());
            }
        }
        return null;
    }

    interface HttpMethod {

        String POST = "POST";

        String GET = "GET";

        String PUT = "PUT";

    }


}
