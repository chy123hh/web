package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Task;

import java.util.List;

/**
 * 任务数据访问层接口
 * 使用 MyBatis-Plus 和原生 MyBatis 混合方式
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 根据状态查询任务列表
     *
     * @param status 任务状态
     * @return 任务列表
     */
    @Select("SELECT * FROM task WHERE status = #{status} ORDER BY create_time DESC")
    List<Task> selectByStatus(@Param("status") String status);

    /**
     * 根据用户 ID 查询发布的任务列表
     *
     * @param userId 用户 ID
     * @return 任务列表
     */
    @Select("SELECT * FROM task WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Task> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据接单人 ID 查询任务列表
     *
     * @param acceptorId 接单人 ID
     * @return 任务列表
     */
    @Select("SELECT * FROM task WHERE acceptor_id = #{acceptorId} ORDER BY create_time DESC")
    List<Task> selectByAcceptorId(@Param("acceptorId") Long acceptorId);

    /**
     * 更新任务状态和接单人
     *
     * @param id         任务 ID
     * @param status     新状态
     * @param acceptorId 新接单人 ID
     * @return 影响行数
     */
    @Update("UPDATE task SET status = #{status}, acceptor_id = #{acceptorId} WHERE id = #{id}")
    int updateStatusAndAcceptor(@Param("id") Long id, @Param("status") String status,
            @Param("acceptorId") Long acceptorId);

    /**
     * 分页查询指定状态的任务
     * 
     * @param page   分页对象
     * @param status 任务状态
     * @return 分页结果
     */
    Page<Task> selectByStatusWithPagination(Page<Task> page, @Param("status") String status);

    /**
     * 分页查询指定用户发布的任务
     * 
     * @param page   分页对象
     * @param userId 用户 ID
     * @return 分页结果
     */
    Page<Task> selectByUserIdWithPagination(Page<Task> page, @Param("userId") Long userId);

    /**
     * 分页查询指定用户接单的任务
     * 
     * @param page       分页对象
     * @param acceptorId 接单用户 ID
     * @return 分页结果
     */
    Page<Task> selectByAcceptorIdWithPagination(Page<Task> page, @Param("acceptorId") Long acceptorId);
}
