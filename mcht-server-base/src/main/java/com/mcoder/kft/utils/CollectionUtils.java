package com.mcoder.kft.utils;

import com.mcoder.kft.cst.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2018/8/13 20:47
 */
public class CollectionUtils {

    /**
     * Map字典升序排序
     *
     * @param params
     * @return
     */
    public static String sort(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        Map<String, String> sortMap = new TreeMap(Comparator.naturalOrder());
        sortMap.putAll(params);
        sortMap.forEach((key, value) -> builder.append(key).append(Constants.Separate.EQUAL).append(value).append(Constants.Separate.BIT_AND));
        return builder.toString().replaceAll("&$", "");
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param params 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> filterNullVal(Map<String, String> params) {
        Map<String, String> result = new HashMap<>(16);
        if (params == null || params.size() <= 0) {
            return result;
        }

        params.forEach((key, value) -> {
            if (StringUtils.isNotEmpty(value)) {
                result.put(key, value);
            }
        });
        return result;
    }

    /**
     * 将Map中的数据转换成key1=value1&key2=value2的形式
     *
     * @param data       待拼接的Map数据
     * @param filterNull 是否需要过滤空值  true：需要  false：不需要
     * @param filterKeys 需要过滤的key
     * @return 拼接好后的字符串
     */
    public static String coverMap2String(Map<String, String> data, boolean filterNull, String... filterKeys) {
        TreeMap<String, String> tree = new TreeMap<>(data);
        StringBuffer buffer = new StringBuffer();
        tree.forEach((key, value) -> {
            // 过滤空值
            if (filterNull && org.apache.commons.lang3.StringUtils.isBlank(value)) {
                return;
            }
            // 过滤指定的key
            if (null != filterKeys && Arrays.asList(filterKeys).contains(key)) {
                return;
            }
            buffer.append(key).append(Constants.Separate.EQUAL).append(value).append(Constants.Separate.BIT_AND);
        });
        return buffer.substring(0, buffer.length() - 1);
    }

    /**
     * 解析 a=1&b=2&c=3 格式为Map
     *
     * @param result
     * @return
     */
    public static Map formatData(String result) {
        if (org.apache.commons.lang3.StringUtils.isBlank(result)) {
            return null;
        }
        String[] data = result.split(Constants.Separate.BIT_AND);
        if (data.length <= 0) {
            return null;
        }
        Map dataMap = new TreeMap<>();
        String[] tmp;
        for (String entry : data) {
            tmp = entry.split(Constants.Separate.EQUAL, 2);
            if (tmp.length > 1) {
                dataMap.put(tmp[0], tmp[1]);
            }
        }
        return dataMap;
    }
}
