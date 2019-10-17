package com.mcoder.kft.mapper;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public interface BaseMapper<T> {

    int insert(T t);

    int update(T t);

    T selectById(int id);

}
