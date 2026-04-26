package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.AdminUser;

@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
