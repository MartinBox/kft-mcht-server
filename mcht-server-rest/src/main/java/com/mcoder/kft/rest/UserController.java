package com.mcoder.kft.rest;

import com.mcoder.kft.Assert;
import com.mcoder.kft.MailHandler;
import com.mcoder.kft.ServiceException;
import com.mcoder.kft.cst.Constants;
import com.mcoder.kft.entity.User;
import com.mcoder.kft.service.UserService;
import com.mcoder.kft.utils.TimeUtils;
import com.mcoder.kft.vo.LoginVO;
import com.mcoder.kft.vo.ResultVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@RestController
public class UserController extends BaseController implements MailHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;
    @Value("${spring.mail.title}")
    private String title;
    @Value("${spring.mail.text}")
    private String text;

    @RequestMapping(value = "/identify_send", method = RequestMethod.POST)
    public ResultVO email(@RequestBody LoginVO loginDto) throws ServiceException {
        if (StringUtils.isBlank(loginDto.getUserName()) || StringUtils.isBlank(loginDto.getPassword())) {
            return ResultVO.builder().retCode(Constants.RET_CODE_FAIL).retDesc("用户或密码不能为空").build();
        }

        User user = userService.selectByUsername(loginDto.getUserName());
        Assert.notNull(user, "用户不存在");
        Assert.isTrue(user.getPassword().equals(loginDto.getPassword()), "密码不正确");
        Assert.notBlank(user.getEmail(), "用户未设置邮箱");

        String veriCode = RandomStringUtils.randomNumeric(6);
        logger.info("login identify code :{} ", veriCode);
        setCurrentEmailCode(loginDto, veriCode);
        sendSimpleMail(mailSender, fromEmail, user.getEmail(), title, String.format(text, veriCode));
        return ResultVO.builder().build().success();
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("login").addObject("title", getTitle());
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResultVO logout() {
        request.getSession().removeAttribute(SESSION_USER_NAME);
        return ResultVO.builder().retDesc("退出成功").build().success();
    }


    @RequestMapping(value = "/user_login", method = RequestMethod.POST)
    public ResultVO login(@RequestBody LoginVO loginVO, HttpServletRequest request) throws ServiceException {
        Assert.notBlank(loginVO.getUserName(), "请输入用户名");
        Assert.notBlank(loginVO.getPassword(), "请输入密码");

        User user = userService.selectByUsername(loginVO.getUserName());
        Assert.notNull(user, "用户不存在");
        if (loginConfiguration.isPwdCheck()) {
            Assert.isTrue(user.getPassword().equals(loginVO.getPassword()), "密码不正确");
        }
        validateEmailCode(loginVO);
        setCurrentUserName(loginVO);

        return ResultVO.builder().build().success();
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResultVO update(@RequestBody LoginVO loginDto) throws ServiceException {
        Assert.notBlank(loginDto.getUserName(), "请输入用户名");
        Assert.notBlank(loginDto.getPassword(), "请输入密码");
        User user = userService.selectByUsername(loginDto.getUserName());
        Assert.notNull(user, "用户不存在");

        user.setPassword(loginDto.getPassword());
        user.setUpdateTime(TimeUtils.getCurrentDateString());
        user.setEmail(loginDto.getEmail());
        boolean update = userService.update(user);
        Assert.isTrue(update, "更新失败");
        return ResultVO.builder().retDesc("更新成功").build().success();
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResultVO insert(@RequestBody LoginVO loginDto) throws ServiceException {
        Assert.notBlank(loginDto.getUserName(), "请输入用户名");
        Assert.notBlank(loginDto.getPassword(), "请输入密码");
        Assert.notBlank(loginDto.getEmail(), "请输入邮箱");

        User user = userService.selectByUsername(loginDto.getUserName());
        Assert.isNull(user, "用户名已存在");

        user = User.builder()
                .userName(loginDto.getUserName())
                .password(loginDto.getPassword())
                .email(loginDto.getEmail())
                .insertTime(TimeUtils.getCurrentDateString())
                .updateTime(TimeUtils.getCurrentDateString())
                .build();
        boolean insert = userService.insert(user);
        Assert.isTrue(insert, "新增失败");

        return ResultVO.builder().retDesc("新增成功").build().success();
    }

    @RequestMapping(value = "/user/page", method = RequestMethod.GET)
    public ResultVO page(String userName, int page, int rows) {
        return userService.selectByPage(User.builder().userName(userName).build(), page, rows);
    }

}
