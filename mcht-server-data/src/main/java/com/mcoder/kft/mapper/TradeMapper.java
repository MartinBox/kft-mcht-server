package com.mcoder.kft.mapper;

import com.mcoder.kft.entity.Trade;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public interface TradeMapper extends BaseMapper<Trade> {
    Trade selectByOrderNo(String orderNo);

    List<Trade> selectByPage(Trade user);
}
