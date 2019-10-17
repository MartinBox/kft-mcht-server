package com.mcoder.kft;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceException extends Exception {
    private String errorCode;
    private String errorDesc;
}
