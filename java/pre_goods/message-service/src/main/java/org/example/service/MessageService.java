package org.example.service;

import org.example.common.dto.Result;
import org.example.entity.Message;

public interface MessageService {

    Result sendMessage(Message message);

    Result getMyMessages();

    Result getUnreadMessages();

    Result getUnreadCount();

    Result markAsRead(Long messageId);

    Result markAllAsRead();

    Result deleteMessage(Long messageId);
}
