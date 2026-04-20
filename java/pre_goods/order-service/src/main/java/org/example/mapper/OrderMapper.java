package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Order;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM delivery_order WHERE order_no = #{orderNo}")
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM delivery_order WHERE taker_id = #{takerId} ORDER BY create_time DESC")
    List<Order> selectByTakerId(@Param("takerId") Long takerId);

    @Select("SELECT * FROM delivery_order WHERE publisher_id = #{publisherId} ORDER BY create_time DESC")
    List<Order> selectByPublisherId(@Param("publisherId") Long publisherId);

    @Select("SELECT * FROM delivery_order WHERE task_id = #{taskId}")
    Order selectByTaskId(@Param("taskId") Long taskId);

    @Update("UPDATE delivery_order SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE delivery_order SET complete_proof_url = #{proofUrl}, status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateCompleteProof(@Param("id") Long id, @Param("proofUrl") String proofUrl, @Param("status") Integer status);

    @Update("UPDATE delivery_order SET status = #{status}, confirm_time = NOW(), update_time = NOW() WHERE id = #{id}")
    int updateConfirmStatus(@Param("id") Long id, @Param("status") Integer status);
}
