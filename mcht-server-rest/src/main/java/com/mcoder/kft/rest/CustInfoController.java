package com.mcoder.kft.rest;

import com.mcoder.kft.Assert;
import com.mcoder.kft.ServiceException;
import com.mcoder.kft.cst.Constants;
import com.mcoder.kft.entity.CustInfo;
import com.mcoder.kft.service.CustInfoService;
import com.mcoder.kft.utils.TimeUtils;
import com.mcoder.kft.vo.ResultVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@RestController
@RequestMapping("/cust_info")
public class CustInfoController {

    @Autowired
    private CustInfoService custInfoService;

    @RequestMapping(method = RequestMethod.GET)
    public ResultVO page(CustInfo custInfo, int page, int rows) {
        return custInfoService.selectByPage(custInfo, page, rows);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResultVO insert(@RequestBody CustInfo custInfo) throws ServiceException {
        if (StringUtils.isBlank(custInfo.getCustBankNo())
                || StringUtils.isBlank(custInfo.getCustBankAccountNo())
                || StringUtils.isBlank(custInfo.getCustName())) {
            return ResultVO.builder().retDesc("必填参数不能为空").build().fail();
        }
        custInfo.setInsertTime(TimeUtils.getCurrentDateString());
        custInfo.setTreatyStatus(Constants.TreatyStatus.INIT);
        custInfo.setMerchantTreatyNo(UUID.randomUUID().toString().replaceAll("-", ""));
        boolean insert = custInfoService.insert(custInfo);
        Assert.isTrue(insert, "新增失败");
        return ResultVO.builder().retDesc("新增成功").build().success();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResultVO update(@RequestBody CustInfo custInfo) throws ServiceException {
        if (StringUtils.isBlank(custInfo.getCustBankNo())
                || StringUtils.isBlank(custInfo.getCustBankAccountNo())
                || StringUtils.isBlank(custInfo.getCustName())) {
            return ResultVO.builder().retDesc("必填参数不能为空").build().fail();
        }
        custInfo.setInsertTime(TimeUtils.getCurrentDateString());
        boolean update = custInfoService.update(custInfo);
        Assert.isTrue(update, "修改失败");
        return ResultVO.builder().retDesc("修改成功").build().success();
    }
}
