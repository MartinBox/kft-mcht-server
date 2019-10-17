package com.mcoder.kft.mapper;

import com.mcoder.kft.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public interface UserMapper extends BaseMapper<User> {

    List<User> list();

    User selectByUsername(@Param("userName") String userName);

    List<User> selectByPage(User user);
}
