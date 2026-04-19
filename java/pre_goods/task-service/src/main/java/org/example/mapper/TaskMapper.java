package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Task;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    @Select("SELECT * FROM task WHERE status = #{status} ORDER BY create_time DESC")
    List<Task> selectByStatus(@Param("status") String status);

    @Select("SELECT * FROM task WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Task> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM task WHERE acceptor_id = #{acceptorId} ORDER BY create_time DESC")
    List<Task> selectByAcceptorId(@Param("acceptorId") Long acceptorId);

    @Update("UPDATE task SET status = #{status}, acceptor_id = #{acceptorId} WHERE id = #{id}")
    int updateStatusAndAcceptor(@Param("id") Long id, @Param("status") String status, @Param("acceptorId") Long acceptorId);
}
