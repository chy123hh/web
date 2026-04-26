package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Task;

public interface TaskMapper extends BaseMapper<Task> {

    IPage<Task> searchTasks(
            Page<Task> page,
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("minReward") Double minReward,
            @Param("maxReward") Double maxReward,
            @Param("status") String status
    );
}