
package com.mcoder.kft.web;

import com.alibaba.fastjson.serializer.PropertyFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志打印过滤
 *
 * @author: weiyuanhua
 * @since: 2017-07-15
 */
public class JsonPropertyFilter implements PropertyFilter {

    private final Set<String> excludes = new HashSet<String>();

    public JsonPropertyFilter(String... properties) {
        for (String item : properties) {
            if (item != null) {
                this.excludes.add(item);
            }
        }
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    @Override
    public boolean apply(Object object, String name, Object value) {

        if (this.excludes.contains(name)) {
            return false;
        }
        return true;
    }
}

