package com.mcoder.kft.rest;

import com.mcoder.kft.Assert;
import com.mcoder.kft.KftChannelConfiguration;
import com.mcoder.kft.PaymentService;
import com.mcoder.kft.ServiceException;
import com.mcoder.kft.cst.Constants;
import com.mcoder.kft.dto.KftResultDto;
import com.mcoder.kft.entity.CustInfo;
import com.mcoder.kft.entity.Trade;
import com.mcoder.kft.service.CustInfoService;
import com.mcoder.kft.service.TradeService;
import com.mcoder.kft.vo.ResultVO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@RestController
public class TradeController extends BaseController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private KftChannelConfiguration kftChannelConfiguration;
    @Autowired
    private CustInfoService custInfoService;

    /**
     * 分页查询
     *
     * @param trade
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/trade_batch_query", method = RequestMethod.GET)
    public ResultVO page(Trade trade, int page, int rows) {
        return tradeService.selectByPage(trade, page, rows);
    }

    @RequestMapping(value = "/trade_record_query/{orderNo}", method = RequestMethod.GET)
    public ResultVO query(@PathVariable("orderNo") String orderNo) throws Exception {
        Assert.notBlank(orderNo, "订单号不能为空");
        return paymentService.query(Trade.builder().orderNo(orderNo).merchantId(getCurrentUserName()).build());
    }

    @RequestMapping(value = "/gbp_pay", method = RequestMethod.POST)
    public ResultVO trade(@RequestBody Trade trade) throws Exception {
        trade.setMerchantId(getCurrentUserName());
        return paymentService.pay(trade);
    }

    @RequestMapping(value = "/query_available_balance", method = RequestMethod.GET)
    public ResultVO queryAvailableBalance() throws Exception {
        KftResultDto kftResultDto = paymentService.queryAvailableBalance(Trade.builder().merchantId(getCurrentUserName()).build());
        return ResultVO.builder()
                .availableBalance(kftResultDto.getAvailableBalance())
                .build()
                .success();
    }

    @RequestMapping(value = "/capital_account_balance_change_query", method = RequestMethod.GET)
    public ResultVO<String> queryAvailableChange(String tradeDate) throws Exception {
        Assert.notBlank(tradeDate, "必填参数不能为空");
        KftResultDto kftResultDto = paymentService.queryAvailableChange(Trade.builder().merchantId(getCurrentUserName()).tradeDate(tradeDate).build());

        //返回数据
        return ResultVO.builder()
                .url("/capital_account_balance_change_download/" + kftResultDto.getFileName())
                .build()
                .success();
    }

    @RequestMapping(value = "/capital_account_balance_change_download/{fileName}", method = RequestMethod.GET)
    public void downloadAvailableChange(HttpServletResponse response, @PathVariable("fileName") String fileName) throws IOException, ServiceException {
        Assert.notBlank(fileName, "fileName不能为空");
        String encodeFileName = URLEncoder.encode("资金变动明细" + fileName + ".txt", Constants.CHARSET_UTF_8_NAME);
        response.setContentType("application/octet-stream");
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.addHeader("charset", "utf-8");
        response.addHeader("Pragma", "no-cache");
        response.setHeader("Content-Disposition", "attachment; filename=" + encodeFileName + "; ");
        ServletOutputStream outputStream = null;
        InputStream inputStream = null;
        try {

            inputStream = new FileInputStream(String.format(kftChannelConfiguration.getFiles().getBalanceChangeFile(), fileName));
            outputStream = response.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
            response.flushBuffer();
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    @RequestMapping(value = "/recon_result_query_sftp/{checkedDate}", method = RequestMethod.GET)
    public ResultVO<String> reconResultQuerySftp(@PathVariable("checkedDate") String checkedDate) throws Exception {
        paymentService.reconResultQuerySftp(Trade.builder().merchantId(getCurrentUserName()).checkedDate(checkedDate).build());

        //返回数据
        return ResultVO.builder()
                .url("/download_result_query_sftp/" + checkedDate)
                .build()
                .success();
    }

    @RequestMapping(value = "/download_result_query_sftp/{checkedDate}", method = RequestMethod.GET)
    public void downloadResultQuerySftp(HttpServletResponse response, @PathVariable("checkedDate") String checkedDate) throws IOException {
        String encodeFileName = URLEncoder.encode("对账文件" + checkedDate + ".zip", Constants.CHARSET_UTF_8_NAME);
        response.setContentType("application/octet-stream");
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.addHeader("charset", "utf-8");
        response.addHeader("Pragma", "no-cache");
        response.setHeader("Content-Disposition", "attachment; filename=" + encodeFileName + "; ");
        ServletOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(String.format(kftChannelConfiguration.getFiles().getSftpFile(), checkedDate));
            outputStream = response.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
            response.flushBuffer();
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * 开通协议
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/gbp_send_treaty_record_to_kft/{custId}", method = RequestMethod.GET)
    public ResultVO sendTreaty(@PathVariable("custId") Integer id) throws Exception {
        CustInfo custInfo = custInfoService.selectById(id);
        return paymentService.sendTreaty(custInfo, getCurrentUserName());
    }

    /**
     * 查询协议
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/gbp_query_treaty_record_info/{custId}", method = RequestMethod.GET)
    public ResultVO sendTreatyQuery(@PathVariable("custId") Integer id) throws Exception {
        CustInfo custInfo = custInfoService.selectById(id);
        return paymentService.sendTreatyQuery(custInfo, getCurrentUserName());
    }
}
