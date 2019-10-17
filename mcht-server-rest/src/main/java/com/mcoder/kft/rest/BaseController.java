package com.mcoder.kft.rest;

import com.mcoder.kft.Assert;
import com.mcoder.kft.LoginConfiguration;
import com.mcoder.kft.ServiceException;
import com.mcoder.kft.cst.Constants;
import com.mcoder.kft.vo.LoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public class BaseController {
    public Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    public LoginConfiguration loginConfiguration;
    @Value("${server.title}")
    private String title;

    public final String SESSION_USER_NAME = "userName";

    private static Map<String, String> loginMap = new ConcurrentHashMap<>();

    private static Map<String, String> emailCodeMap = new ConcurrentHashMap<>();

    public String getCurrentUserName() throws IOException {
        Object userName = request.getSession().getAttribute(SESSION_USER_NAME);
        if (null == userName) {
            logger.info("session userName is null,redirect login page");
            response.sendRedirect("login");
        }
        return String.valueOf(userName);
    }

    public void setCurrentUserName(LoginVO loginVO) {
        loginMap.put(loginVO.getUserName(), request.getSession().getId());
        request.getSession().setAttribute(SESSION_USER_NAME, loginVO.getUserName());
    }

    public void setCurrentEmailCode(LoginVO loginVO, String code) {
        emailCodeMap.put(loginVO.getUserName(), code);
    }

    public void validateEmailCode(LoginVO loginVO) throws ServiceException {
        // 关闭邮箱验证
        if (!loginConfiguration.isEmailCheck()) {
            return;
        }
        Assert.notBlank(loginVO.getIdentifyCode(), "请输入登录校验码");
        if (emailCodeMap.isEmpty()) {
            throw new ServiceException(Constants.RET_CODE_FAIL, "请先发送登录校验码");
        }
        boolean result = loginVO.getIdentifyCode().equals(emailCodeMap.get(loginVO.getUserName()));
        Assert.isTrue(result, "登录校验码不正确或已失效，请重新输入");
        if (result) {
            emailCodeMap.remove(loginVO.getUserName());
        }
    }

    public String getTitle() {
        return title;
    }
}
