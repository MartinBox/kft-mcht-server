package com.mcoder.kft.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mcoder.kft.entity.CustInfo;
import com.mcoder.kft.mapper.CustInfoMapper;
import com.mcoder.kft.service.CustInfoService;
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
public class CustInfoServiceImpl implements CustInfoService {
    @Autowired
    private CustInfoMapper custInfoMapper;

    @Override
    public boolean insert(CustInfo custInfo) {
        return custInfoMapper.insert(custInfo) == 1 ? true : false;
    }

    @Override
    public boolean update(CustInfo custInfo) {
        return custInfoMapper.update(custInfo) == 1 ? true : false;
    }

    @Override
    public CustInfo selectById(int id) {
        return custInfoMapper.selectById(id);
    }

    @Override
    public ResultVO<CustInfo> selectByPage(CustInfo custInfo, int page, int rows) {
        Page pageResult = PageHelper.startPage(page, rows);
        List<CustInfo> custInfos = custInfoMapper.selectByPage(custInfo);

        ResultVO<CustInfo> resultVO = new ResultVO<>();
        resultVO.success();
        resultVO.setTotal(pageResult.getTotal());
        resultVO.setRows(custInfos);
        return resultVO;
    }

    @Override
    public boolean updateTreatyInfo(CustInfo custInfo) {
        return custInfoMapper.updateTreatyInfo(custInfo) == 1 ? true : false;
    }
}
