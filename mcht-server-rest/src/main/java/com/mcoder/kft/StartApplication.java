package com.mcoder.kft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019/1/3 10:29
 */
@SpringBootApplication
@ServletComponentScan(value = {"com.mcoder"})
public class StartApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(StartApplication.class);
        application.addListeners(new ApplicationStartup());
        application.run(args);
    }
}
