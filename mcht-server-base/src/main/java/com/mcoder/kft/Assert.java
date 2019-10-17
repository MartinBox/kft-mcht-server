package com.mcoder.kft;

import com.mcoder.kft.cst.Constants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public class Assert {

    public static void notNull(Object o, String desc) throws ServiceException {
        if (null == o) {
            throw new ServiceException(Constants.RET_CODE_FAIL, desc);
        }
    }

    public static void isNull(Object o, String desc) throws ServiceException {
        if (null != o) {
            throw new ServiceException(Constants.RET_CODE_FAIL, desc);
        }
    }

    public static void notBlank(String param, String desc) throws ServiceException {
        if (StringUtils.isBlank(param)) {
            throw new ServiceException(Constants.RET_CODE_FAIL, desc);
        }
    }

    public static void isTrue(boolean b, String desc) throws ServiceException {
        if (!b) {
            throw new ServiceException(Constants.RET_CODE_FAIL, desc);
        }
    }
}
