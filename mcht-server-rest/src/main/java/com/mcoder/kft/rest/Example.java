package com.mcoder.kft.rest;

import com.mcoder.kft.entity.User;
import com.mcoder.kft.service.UserService;
import com.mcoder.kft.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@RestController
public class Example extends BaseController {
    public static String merchantId = "2018102500097289";

    @Autowired
    private UserService userService;

    @RequestMapping("/test")
    String test() {
        setCurrentUserName(LoginVO.builder().userName("test").build());
        return "Hello World!";
    }

    @RequestMapping("/test/db")
    List<User> testDB() {

        return userService.list();
    }
}
