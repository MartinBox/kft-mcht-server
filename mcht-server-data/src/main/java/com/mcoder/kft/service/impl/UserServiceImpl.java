package com.mcoder.kft.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mcoder.kft.entity.User;
import com.mcoder.kft.mapper.UserMapper;
import com.mcoder.kft.service.UserService;
import com.mcoder.kft.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public User selectByUsername(String userName) {
        return userMapper.selectByUsername(userName);
    }

    @Override
    public boolean update(User user) {
        return userMapper.update(user) == 1 ? true : false;
    }

    @Override
    public boolean insert(User user) {
        return userMapper.insert(user) == 1 ? true : false;
    }

    @Override
    public ResultVO<User> selectByPage(User user, int page, int rows) {
        Page pageResult = PageHelper.startPage(page, rows);
        List<User> users = userMapper.selectByPage(user);

        ResultVO<User> resultVO = new ResultVO<>();
        resultVO.success();
        resultVO.setTotal(pageResult.getTotal());
        resultVO.setRows(users);
        return resultVO;
    }
}
