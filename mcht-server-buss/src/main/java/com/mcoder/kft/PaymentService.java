package com.mcoder.kft;

import com.alibaba.fastjson.JSON;
import com.mcoder.kft.cst.Constants;
import com.mcoder.kft.dto.DetailResultDto;
import com.mcoder.kft.dto.KftBaseResultDto;
import com.mcoder.kft.dto.KftResultDto;
import com.mcoder.kft.entity.CustInfo;
import com.mcoder.kft.entity.Trade;
import com.mcoder.kft.net.Https;
import com.mcoder.kft.security.RsaHelper;
import com.mcoder.kft.security.RsaUtils;
import com.mcoder.kft.service.CustInfoService;
import com.mcoder.kft.service.TradeService;
import com.mcoder.kft.utils.CollectionUtils;
import com.mcoder.kft.utils.FtpHelper;
import com.mcoder.kft.utils.TimeUtils;
import com.mcoder.kft.vo.ResultVO;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 1194688733@qq.com
 * @Description: 快付通地址：https://test.kftpay.com.cn/index
 * @date 2019/1/4 12:41
 */
@Service
public class PaymentService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TradeService tradeService;
    @Resource
    private KftChannelContext kftChannelContext;
    @Autowired
    private KftChannelConfiguration kftChannelConfiguration;
    @Autowired
    private CustInfoService custInfoService;

    public ResultVO<String> pay(Trade trade) throws ServiceException {
        trade.setOrderNo(UUID.randomUUID().toString().replaceAll("-", ""));
        Map<String, String> params = new HashMap<>(16);
        params.put("service", "gbp_pay");
        params.put("version", kftChannelConfiguration.getServiceVersion());
        // 替换成快付通提供的商户ID，测试生产不一样
        params.put("merchantId", trade.getMerchantId());
        // 替换成快付通提供的产品编号，测试生产不一样
        params.put("productNo", KftChannelService.getProductMap(trade.getMerchantId()).getTrade());
        // 订单号同一个商户必须保证唯一
        params.put("orderNo", trade.getOrderNo());
        // 简要概括此次交易的内容
        params.put("tradeName", trade.getTradeName());
        // 商户用于付款的银行账户,资金不落地模式时必填（重要参数）
        params.put("merchantBankAccountNo", trade.getMerchantBankAccountNo());
        //对于有些银行账户被扣款时，需要提供此绑定手机号才能进行交易；此手机号和短信通知客户的手机号可以一致，也可以不一致
        params.put("merchantBindPhoneNo", trade.getMerchantBindPhoneNo());
        // 交易时间,注意此时间取值一般为商户方系统时间而非快付通生成此时间
        params.put("tradeTime", TimeUtils.getCurrentDateString());
        // 交易金额，单位：分，不支持小数点
        params.put("amount", trade.getAmount());
        // 快付通定义的扣费币种,详情请看文档
        params.put("currency", "CNY");
        // 客户银行帐户银行别，测试环境只支持：中、农、建
        params.put("custBankNo", trade.getCustBankNo());
        //支行行号,可空
        params.put("custBankAccountIssuerNo", trade.getCustBankAccountIssuerNo());
        // 本次交易中,付款到客户哪张卡
        params.put("custBankAccountNo", trade.getCustBankAccountNo());
        // 客户银行卡户名
        params.put("custName", trade.getCustName());
        // 可空，客户银行卡类型 1：个人，2：企业
        params.put("custBankAcctType", trade.getCustBankAcctType());
        if (Constants.CardType.CREDIT.equals(trade.getCustAccountCreditOrDebit())) {
            // 客户账户借记贷记类型,0存折 1借记 2贷记
            params.put("custAccountCreditOrDebit", Constants.CardType.CREDIT);
            // 可空，信用卡时必填
            params.put("custCardValidDate", trade.getCustCardValidDate());
            // 可空，信用卡时必填
            params.put("custCardCvv2", trade.getCustCardCvv2());
        }

        // 可空，持卡人证件类型，0，目前只支持身份证
        params.put("custCertificationType", trade.getCustCertificationType());
        // 可空，持卡人证件号码
        params.put("custID", trade.getCustID());
        // 可空，如果商户购买的产品中勾选了短信通知功能，则当商户填写了手机号时,快付通会在交易成功后通过短信通知客户
        params.put("custPhone", trade.getCustPhone());
        // 可空,短信内容
        params.put("messages", trade.getMessages());
        // 可空，如果商户购买的产品中勾选了邮件通知功能，则当商户填写了邮箱地址时,快付通会在交易成功后通过邮件通知客户
        params.put("custEmail", trade.getCustEmail());
        // 可空，邮件内容
        params.put("emailMessages", trade.getEmailMessages());
        // 可空，线下协议付款时一般不填
        params.put("custProtocolNo", trade.getCustProtocolNo());
        // 商户可额外填写付款方备注信息,此信息会传给银行,会在银行的账单信息中显示(具体如何显示取决于银行方,快付通不保证银行肯定能显示)
        params.put("remark", trade.getRemark());

        Trade insertTrade = JSON.parseObject(JSON.toJSONString(params), Trade.class);
        insertTrade.setInsertTime(TimeUtils.getCurrentDateString());
        insertTrade.setStatus(LocalStatus.PROCESS);
        boolean insert = tradeService.insert(insertTrade);
        Assert.isTrue(insert, "数据插入异常");
        KftResultDto resultDto = sendAndReceiveByString(params);

        boolean processErrorCode = kftChannelConfiguration.getErrorCode().getProcess().contains(resultDto.getErrorCode());
        ChannelStatus channelStatus = processErrorCode ? ChannelStatus.PROCESS : ChannelStatus.ofType(resultDto.getStatus());
        Assert.notNull(resultDto, String.format("快付通应答数据异常，status=%s未定义", resultDto.getStatus()));

        if (ChannelStatus.SUCCESS.equals(channelStatus)) {
            this.update(trade, ChannelStatus.SUCCESS, resultDto);
        }
        if (!processErrorCode && ChannelStatus.FAIL.equals(channelStatus)) {
            resultDto.setFailureDetails(StringUtils.isBlank(resultDto.getFailureDetails()) ? "付款失败：快付通没有给出失败原因" : resultDto.getFailureDetails());
            this.update(trade, ChannelStatus.FAIL, resultDto);
            throw new ServiceException(resultDto.getErrorCode(), "付款失败。原因：" + resultDto.getFailureDetails());
        }
        return ResultVO.builder().retDesc(channelStatus.isFinal() ? "付款成功" : "付款处理中").build().success();
    }

    public ResultVO<String> query(Trade trade) throws ServiceException {
        String orderNo = trade.getOrderNo();
        trade = tradeService.selectByOrderNo(orderNo);
        Assert.notNull(trade, String.format("找不到对应的订单记录：%s", orderNo));
        Map<String, String> params = new HashMap<>(16);
        params.put("service", "trade_record_query");
        params.put("version", kftChannelConfiguration.getServiceVersion());
        params.put("merchantId", trade.getMerchantId());
        params.put("productNo", KftChannelService.getProductMap(trade.getMerchantId()).getTradeQuery());
        params.put("queryCriteria", String.format("%s,,%s", KftChannelService.getProductMap(trade.getMerchantId()).getTrade(), trade.getOrderNo()));
        KftResultDto resultDto = sendAndReceiveByString(params);

        if (org.springframework.util.CollectionUtils.isEmpty(resultDto.getDetails())) {
            throw new ServiceException(Constants.RET_CODE_FAIL, resultDto.getFailureDetails());
        }
        DetailResultDto detailResultDto = resultDto.getDetails().get(0);

        boolean failErrorCode = kftChannelConfiguration.getErrorCode().getFail().contains(detailResultDto.getErrorCode());
        if (ChannelStatus.FAIL.getStatus().equals(detailResultDto.getStatus()) && trade.getStatus().equals(LocalStatus.FAIL)) {
            throw new ServiceException(Constants.RET_CODE_FAIL, detailResultDto.getFailureDetails());
        } else if (LocalStatus.FAIL.equals(detailResultDto.getStatus()) && !trade.getStatus().equals(LocalStatus.FAIL)) {
            this.update(trade, ChannelStatus.FAIL, detailResultDto);
            throw new ServiceException(trade.getErrorCode(), trade.getErrorDesc());
        }
        ChannelStatus channelStatus = failErrorCode ? ChannelStatus.FAIL : ChannelStatus.ofType(detailResultDto.getStatus());
        Assert.notNull(channelStatus, String.format("查询操作失败。快付通应答数据异常，status=%s未定义", detailResultDto.getStatus()));
        if (channelStatus.isFinal() && !trade.getStatus().equals(channelStatus.getLocalStatus())) {
            this.update(trade, channelStatus, detailResultDto);
        }
        return ResultVO.builder().retDesc(ChannelStatus.SUCCESS.equals(channelStatus) ? "付款成功" : "操作成功").build().success();

    }

    private void update(Trade trade, ChannelStatus channelStatus, KftBaseResultDto resultDto) {
        Trade updateTrade = Trade.builder()
                .orderNo(trade.getOrderNo())
                .status(channelStatus.getLocalStatus())
                .updateTime(TimeUtils.getCurrentDateString())
                .errorCode(resultDto.getErrorCode())
                .errorDesc(resultDto.getFailureDetails())
                .bankReturnTime(resultDto.getBankReturnTime())
                .build();
        boolean update = tradeService.update(updateTrade);
        logger.error("update result -> {},orderNo -> {},status -> {}", update, updateTrade.getOrderNo(), updateTrade.getStatus());
    }

    public KftResultDto queryAvailableBalance(Trade trade) throws ServiceException {
        Map<String, String> params = new HashMap<>();
        params.put("service", "query_available_balance");
        params.put("version", kftChannelConfiguration.getServiceVersion());
        params.put("merchantId", trade.getMerchantId());
        params.put("productNo", KftChannelService.getProductMap(trade.getMerchantId()).getQueryAvailableBalance());
        KftResultDto resultDto = sendAndReceiveByString(params);
        Assert.notBlank(resultDto.getAvailableBalance(), String.format("查询资金余额失败->%s", resultDto.getFailureDetails()));
        return resultDto;
    }

    public KftResultDto reconResultQuerySftp(Trade trade) throws ServiceException {
        Map<String, String> params = new HashMap<>();
        params.put("service", "recon_result_query_sftp");
        params.put("version", kftChannelConfiguration.getServiceVersion());
        params.put("merchantId", trade.getMerchantId());
        params.put("productNo", KftChannelService.getProductMap(trade.getMerchantId()).getSfptQuery());
        params.put("checkedDate", trade.getCheckedDate());
        KftResultDto resultDto = sendAndReceiveByString(params);
        Assert.notBlank(resultDto.getFileAbsolutePath(), String.format("获取对账文件失败->%s", resultDto.getFailureDetails()));
        KftChannelConfiguration.SftpConfiguration sftp = kftChannelConfiguration.getSftp();
        String file = String.format(kftChannelConfiguration.getFiles().getSftpFile(), trade.getCheckedDate());
        FtpHelper.downloadFileToByte(sftp.getDomain(), sftp.getPort(), sftp.getAccountName(), sftp.getPassword(), sftp.getDir(), resultDto.getFileAbsolutePath(), file);
        return resultDto;
    }

    public KftResultDto queryAvailableChange(Trade trade) throws ServiceException {
        Map<String, String> params = new HashMap<>();
        params.put("service", "capital_account_balance_change_query");
        params.put("version", kftChannelConfiguration.getServiceVersion());
        params.put("merchantId", trade.getMerchantId());
        params.put("productNo", KftChannelService.getProductMap(trade.getMerchantId()).getQueryAvailableBalance());
        params.put("tradeDate", trade.getTradeDate());

        String storePath = String.format(kftChannelConfiguration.getFiles().getBalanceChangeFile(), trade.getTradeDate());
        KftResultDto resultDto = sendAndReceiveByByte(storePath, params, 60);
        Assert.notBlank(resultDto.getFileAbsolutePath(), String.format("查询资金变动失败->%s", resultDto.getFailureDetails()));
        resultDto.setFileName(new File(storePath).getName());
        return resultDto;
    }

    public KftResultDto sendAndReceiveByString(Map<String, String> params) throws ServiceException {
        String service = params.get("service");
        String result = null;
        try {
            this.common(kftChannelContext.findStrategy(params.get("merchantId")), service, params);
            result = Https.anInstance(TimeUnit.SECONDS, 10)
                    .trustAnyServer()
                    .url(String.format(Constants.KFT_URL_FORMAT, kftChannelConfiguration.getHttp().getDomain(), kftChannelConfiguration.getHttp().getPort()))
                    .formBody(params)
                    .post()
                    .execute();

            logger.info("[kft][{}][response]:{}", service, result);
        } catch (Exception e) {
            logger.info("[kft][{}][request]throw exception", service, e);
            throw new ServiceException(Constants.RET_CODE_FAIL, "系统异常");
        }
        Assert.notBlank(result, "快付通请求异常，返回数据为空");
        return JSON.parseObject(result, KftResultDto.class);
    }

    public KftResultDto sendAndReceiveByByte(String file, Map<String, String> params, int timeout) throws ServiceException {
        String result = null;
        String service = params.get("service");
        try {
            this.common(kftChannelContext.findStrategy(params.get("merchantId")), service, params);
            InputStream inputStream = Https.anInstance(TimeUnit.SECONDS, timeout)
                    .trustAnyServer()
                    .url(String.format(Constants.KFT_URL_FILE_FORMAT, kftChannelConfiguration.getHttp().getDomain(), kftChannelConfiguration.getHttp().getPort()))
                    .formBody(params)
                    .header("Accept", "application/octet-stream")
                    .post()
                    .executeStream();

            byte[] buffer = new byte[4];
            inputStream.read(buffer, 0, 4);
            int resultLength = bytes2int(buffer);
            logger.info("byte length:{}", resultLength);
            buffer = new byte[resultLength];
            inputStream.read(buffer, 0, resultLength);
            result = new String(buffer, Constants.CHARSET_UTF_8_NAME);

            ZipArchiveInputStream archiveInputStream = new ZipArchiveInputStream(new BufferedInputStream(inputStream));
            try {
                while (archiveInputStream.getNextZipEntry() != null) {
                    BufferedOutputStream outputStream = null;
                    try {
                        outputStream = new BufferedOutputStream(new FileOutputStream(file), 8192);
                        IOUtils.copy(archiveInputStream, outputStream);
                    } finally {
                        IOUtils.closeQuietly(outputStream);
                    }
                }
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            logger.info("[kft][{}][response]:{}", service, result);
        } catch (Exception e) {
            logger.info("[kft][{}][request]throw exception", service, e);
            throw new ServiceException(Constants.RET_CODE_FAIL, "系统异常");
        }
        Assert.notBlank(result, "快付通请求异常，返回数据为空");
        return JSON.parseObject(result, KftResultDto.class);
    }

    public static int bytes2int(byte[] b) {
        return bytes2int(b, 0);
    }

    public static int bytes2int(byte[] b, int off) {
        return ((b[off + 3] & 255) << 0) + ((b[off + 2] & 255) << 8) + ((b[off + 1] & 255) << 16) + (b[off + 0] << 24);
    }


    private void common(RsaHelper rsaHelper, String service, Map<String, String> params) throws UnsupportedEncodingException {
        params.put("reqNo", UUID.randomUUID().toString().replaceAll("-", ""));
        params.put("callerIp", kftChannelConfiguration.getClientIp());
        params.put("language", kftChannelConfiguration.getLanguage());
        String signStr = CollectionUtils.coverMap2String(params, true, null);
        params.put("signatureInfo", RsaUtils.sign(signStr.getBytes(Constants.CHARSET_UTF_8_NAME), rsaHelper.getX509Certificate(), rsaHelper.getPrivateKey()));
        params.put("signatureAlgorithm", "RSA");
        logger.info("[kft][{}][request]:{}", service, JSON.toJSONString(params));
    }

    public ResultVO<String> sendTreaty(CustInfo custInfo, String merchantId) throws ServiceException {
        String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
        Map<String, String> params = new HashMap<>(16);
        params.put("service", "gbp_send_treaty_record_to_kft");
        params.put("version", kftChannelConfiguration.getServiceVersion());
        params.put("charset", "utf-8");
        // 替换成快付通提供的商户ID，测试生产不一样
        params.put("merchantId", merchantId);
        // 替换成快付通提供的产品编号，测试生产不一样
        params.put("productNo", KftChannelService.getProductMap(merchantId).getTrade());
        // 订单号同一个商户必须保证唯一
        params.put("orderNo", orderNo);
        // 商户与用户之间的唯一标识号
        params.put("merchantTreatyNo", custInfo.getMerchantTreatyNo());
        // 协议类型：1收款，2付款
        params.put("treatyType", "2");
        // 费项代码
        params.put("paymentItem", custInfo.getPaymentItem());
        // 协议生效时间 日期格式：yyyyMMdd
        params.put("startDate", custInfo.getStartDate());
        // 协议失效时间 日期格式：yyyyMMdd
        params.put("endDate", custInfo.getEndDate());
        // 持卡人姓名
        params.put("holderName", custInfo.getCustName());
        // 快付通定义的扣费币种,详情请看文档
        params.put("currencyType", "CNY");
        // 银行行别，测试环境只支持：中、农、建
        params.put("bankType", custInfo.getCustBankNo());
        // 银行卡类型
        params.put("bankCardType", custInfo.getCustAccountCreditOrDebit());
        // 银行卡号
        params.put("bankCardNo", custInfo.getCustBankAccountNo());
        // 预留手机号码
        params.put("mobileNo", custInfo.getCustPhone());
        // 可空，持卡人证件类型，0，目前只支持身份证
        params.put("certificateType", custInfo.getCustCertificationType());
        // 可空，持卡人证件号码
        params.put("certificateNo", custInfo.getCustID());
        // 协议说明
        params.put("note", custInfo.getNote());

        KftResultDto resultDto = sendAndReceiveByString(params);
        // 0 - 待复核  1 - 生效  2 - 失败  3 - 冻结 6 - 解约
        if (Constants.TreatyStatus.STAT_2.equals(resultDto.getStatus())) {
            resultDto.setFailureDetails(StringUtils.isBlank(resultDto.getFailureDetails()) ? "协议开通失败：快付通没有给出失败原因" : resultDto.getFailureDetails());
            throw new ServiceException(resultDto.getErrorCode(), resultDto.getFailureDetails());
        }
        custInfo.setTreatyOrderNo(orderNo);
        custInfo.setTreatyStatus(resultDto.getStatus());
        custInfo.setTreatyResultDesc(resultDto.getFailureDetails());
        if (Constants.TreatyStatus.STAT_0.equals(resultDto.getStatus())) {

        } else if (Constants.TreatyStatus.STAT_1.equals(resultDto.getStatus())) {
            custInfo.setTreatyNo(resultDto.getTreatyNo());
        } else if (Constants.TreatyStatus.STAT_3.equals(resultDto.getStatus())) {

        } else if (Constants.TreatyStatus.STAT_6.equals(resultDto.getStatus())) {

        }
        custInfoService.updateTreatyInfo(custInfo);
        String treatyResultDesc = StringUtils.isEmpty(resultDto.getFailureDetails()) ? getTreatyResultDesc(resultDto.getStatus()) : resultDto.getFailureDetails();
        return ResultVO.builder().treatyStatus(custInfo.getTreatyStatus()).retDesc(treatyResultDesc).build().success();
    }

    public ResultVO<String> sendTreatyQuery(CustInfo custInfo, String merchantId) throws ServiceException {
        Assert.isTrue(StringUtils.isNotEmpty(custInfo.getTreatyStatus()) && !Constants.TreatyStatus.INIT.equals(custInfo.getTreatyStatus()), "请先签署协议！");
        Map<String, String> params = new HashMap<>(16);
        params.put("service", "gbp_query_treaty_record_info");
        params.put("version", kftChannelConfiguration.getServiceVersion());
        params.put("charset", "utf-8");
        // 签约快付通的唯一用户编号,快付通分配提供给商户，测试生产不一样
        params.put("merchantId", merchantId);
        // 即与协议上送接口的订单号相同。如果订单编号为空时，协议号不可为空
        params.put("orderNo", custInfo.getTreatyOrderNo());

        KftResultDto resultDto = sendAndReceiveByString(params);
        // 0 - 待复核  1 - 生效  2 - 失败  3 - 冻结 6 - 解约
        if (Constants.TreatyStatus.STAT_2.equals(resultDto.getStatus())) {
            resultDto.setFailureDetails(StringUtils.isBlank(resultDto.getFailureDetails()) ? "查询失败：快付通没有给出失败原因" : resultDto.getFailureDetails());
            throw new ServiceException(resultDto.getErrorCode(), resultDto.getFailureDetails());
        }
        // 和当前数据库保存不一致，则更新
        if (!custInfo.getTreatyStatus().equals(resultDto.getStatus())) {
            custInfo.setTreatyStatus(resultDto.getStatus());
            custInfo.setTreatyResultDesc(resultDto.getFailureDetails());
            if (Constants.TreatyStatus.STAT_0.equals(resultDto.getStatus())) {
                resultDto.setFailureDetails(StringUtils.isBlank(resultDto.getFailureDetails()) ? "协议已签署" : resultDto.getFailureDetails());
            } else if (Constants.TreatyStatus.STAT_1.equals(resultDto.getStatus())) {
                custInfo.setTreatyNo(resultDto.getTreatyNo());
                resultDto.setFailureDetails(StringUtils.isBlank(resultDto.getFailureDetails()) ? "协议已签署" : resultDto.getFailureDetails());
            } else if (Constants.TreatyStatus.STAT_3.equals(resultDto.getStatus())) {

            } else if (Constants.TreatyStatus.STAT_6.equals(resultDto.getStatus())) {

            }
            custInfoService.updateTreatyInfo(custInfo);
        }
        String treatyResultDesc = StringUtils.isEmpty(resultDto.getFailureDetails()) ? getTreatyResultDesc(resultDto.getStatus()) : resultDto.getFailureDetails();
        return ResultVO.builder().treatyStatus(custInfo.getTreatyStatus()).retDesc(treatyResultDesc).build().success();
    }

    private String getTreatyResultDesc(String treatyStatus) {
        String treatyResultDesc = "";
        switch (treatyStatus) {
            case Constants.TreatyStatus.STAT_0:
                treatyResultDesc = "协议待复核";
                break;
            case Constants.TreatyStatus.STAT_1:
                treatyResultDesc = "协议已生效";
                break;
            case Constants.TreatyStatus.STAT_2:
                treatyResultDesc = "协议申请失败";
                break;
            case Constants.TreatyStatus.STAT_3:
                treatyResultDesc = "协议已冻结";
                break;
            case Constants.TreatyStatus.STAT_6:
                treatyResultDesc = "协议已解约";
                break;
            default:
        }
        return treatyResultDesc;
    }
}
