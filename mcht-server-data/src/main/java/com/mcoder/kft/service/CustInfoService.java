package com.mcoder.kft.service;

import com.mcoder.kft.entity.CustInfo;
import com.mcoder.kft.vo.ResultVO;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public interface CustInfoService {
    boolean insert(CustInfo custInfo);

    boolean update(CustInfo custInfo);

    /**
     * 更新协议信息
     *
     * @param custInfo
     * @return
     */
    boolean updateTreatyInfo(CustInfo custInfo);

    CustInfo selectById(int id);

    ResultVO<CustInfo> selectByPage(CustInfo custInfo, int page, int rows);

}
