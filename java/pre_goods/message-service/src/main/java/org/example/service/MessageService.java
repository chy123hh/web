package org.example.service;

import org.example.common.dto.Result;
import org.example.entity.Message;

public interface MessageService {

    Result sendMessage(Message message);

    /**
     * 查询我的消息列表（分页）
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    Result getMyMessages(Integer page, Integer size);

    /**
     * 查询未读消息（分页）
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    Result getUnreadMessages(Integer page, Integer size);

    Result getUnreadCount();

    Result markAsRead(Long messageId);

    Result markAllAsRead();

    Result deleteMessage(Long messageId);
}
