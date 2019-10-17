package com.mcoder.kft.service;

import com.mcoder.kft.entity.User;
import com.mcoder.kft.vo.ResultVO;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public interface UserService {
    List<User> list();

    User selectByUsername(String userName);

    boolean update(User user);

    boolean insert(User user);

    ResultVO<User> selectByPage(User user, int page, int rows);
}
