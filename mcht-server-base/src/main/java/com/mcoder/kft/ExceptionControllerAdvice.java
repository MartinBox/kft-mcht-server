package com.mcoder.kft;

import com.mcoder.kft.cst.Constants;
import com.mcoder.kft.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2018/6/29 15:31
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResultVO handleCommonException(ServiceException e) {

        return ResultVO.builder().retCode(Constants.RET_CODE_FAIL).retDesc(e.getErrorDesc()).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultVO handleCommonException(HttpServletRequest request, Exception e) {
        logger.error("uri -> {}", request.getRequestURI(), e);
        ResultVO resultVO = ResultVO.builder().retCode(Constants.RET_CODE_FAIL).build();
        if (e instanceof HttpRequestMethodNotSupportedException) {
            resultVO.setRetDesc(e.getMessage());
        }
        // url 404
        else if (e instanceof NoHandlerFoundException) {
            resultVO.setRetDesc("目标资源未找到，请检查url是否正确");
        } else {
            resultVO.setRetDesc("系统未知异常");
        }
        return resultVO;
    }
}