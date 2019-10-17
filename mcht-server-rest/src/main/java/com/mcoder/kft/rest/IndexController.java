package com.mcoder.kft.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@RestController
public class IndexController extends BaseController {

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView login() throws Exception {
        return new ModelAndView("home")
                .addObject("userName", getCurrentUserName())
                .addObject("title", getTitle());
    }

    @RequestMapping(value = "/trade_record_query.html", method = RequestMethod.GET)
    public ModelAndView trade_record_query() {
        return new ModelAndView("trade_record_query");
    }

    @RequestMapping(value = "/capital_account_balance_change_query.html", method = RequestMethod.GET)
    public ModelAndView capital_account_balance_change_query() {
        return new ModelAndView("capital_account_balance_change_query");
    }

    @RequestMapping(value = "/auto_register.html", method = RequestMethod.GET)
    public ModelAndView auto_register() {
        return new ModelAndView("auto_register");
    }

    @RequestMapping(value = "/recon_result_query_sftp.html", method = RequestMethod.GET)
    public ModelAndView recon_result_query_sftp() {
        return new ModelAndView("recon_result_query_sftp");
    }

    @RequestMapping(value = "/cust_manage.html", method = RequestMethod.GET)
    public ModelAndView cust_manage() {
        return new ModelAndView("cust_manage");
    }


}
