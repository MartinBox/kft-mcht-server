package com.mcoder.kft.service;

import com.mcoder.kft.entity.Trade;
import com.mcoder.kft.vo.ResultVO;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public interface TradeService {
    boolean insert(Trade trade);

    boolean update(Trade trade);

    Trade selectByOrderNo(String orderNo);

    ResultVO<Trade> selectByPage(Trade trade, int page, int rows);
}
