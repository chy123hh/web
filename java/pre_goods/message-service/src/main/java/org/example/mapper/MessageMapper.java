package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Message;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT * FROM message WHERE receiver_id = #{userId} ORDER BY create_time DESC")
    List<Message> selectByReceiverId(@Param("userId") Long userId);

    @Select("SELECT * FROM message WHERE receiver_id = #{userId} AND status = #{status} ORDER BY create_time DESC")
    List<Message> selectByReceiverIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM message WHERE receiver_id = #{userId} AND status = 0")
    Long countUnreadByUserId(@Param("userId") Long userId);

    @Update("UPDATE message SET status = 1, read_time = NOW() WHERE id = #{messageId}")
    int markAsRead(@Param("messageId") Long messageId);

    @Update("UPDATE message SET status = 1, read_time = NOW() WHERE receiver_id = #{userId} AND status = 0")
    int markAllAsRead(@Param("userId") Long userId);
}
