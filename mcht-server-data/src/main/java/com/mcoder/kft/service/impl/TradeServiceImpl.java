package com.mcoder.kft.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mcoder.kft.entity.Trade;
import com.mcoder.kft.mapper.TradeMapper;
import com.mcoder.kft.service.TradeService;
import com.mcoder.kft.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Service
public class TradeServiceImpl implements TradeService {
    @Autowired
    private TradeMapper tradeMapper;

    @Override
    public boolean insert(Trade trade) {
        return tradeMapper.insert(trade) == 1 ? true : false;
    }

    @Override
    public boolean update(Trade trade) {
        return tradeMapper.update(trade) == 1 ? true : false;
    }

    @Override
    public Trade selectByOrderNo(String orderNo) {
        return tradeMapper.selectByOrderNo(orderNo);
    }

    @Override
    public ResultVO<Trade> selectByPage(Trade trade, int page, int rows) {
        Page pageResult = PageHelper.startPage(page, rows);
        List<Trade> trades = tradeMapper.selectByPage(trade);

        ResultVO<Trade> resultVO = new ResultVO<>();
        resultVO.success();
        resultVO.setTotal(pageResult.getTotal());
        resultVO.setRows(trades);
        return resultVO;
    }
}
