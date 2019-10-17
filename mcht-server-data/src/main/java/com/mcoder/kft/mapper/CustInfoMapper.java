package com.mcoder.kft.mapper;

import com.mcoder.kft.entity.CustInfo;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public interface CustInfoMapper extends BaseMapper<CustInfo> {


    List<CustInfo> selectByPage(CustInfo user);

    /**
     * 更新协议信息
     *
     * @param custInfo
     * @return
     */
    int updateTreatyInfo(CustInfo custInfo);
}
