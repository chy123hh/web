package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.User;

public interface UserMapper extends BaseMapper<User> {

  User selectByStudentId(String studentId);

  boolean existsByStudentId(String studentId);
}
