package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Result;
import org.example.common.util.JwtUtil;
import org.example.entity.Message;
import org.example.mapper.MessageMapper;
import org.example.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageMapper messageMapper;
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public Result sendMessage(Message message) {
        Long senderId = getCurrentUserId();
        if (senderId == null) {
            return Result.error(401, "用户未登录");
        }

        message.setSenderId(senderId);
        message.setStatus(Message.STATUS_UNREAD);
        message.setCreateTime(LocalDateTime.now());

        boolean saved = save(message);
        if (saved) {
            log.info("用户 {} 发送消息给 {}，消息ID: {}", senderId, message.getReceiverId(), message.getId());
            return Result.success("发送消息成功", message.getId());
        }
        return Result.error(500, "发送消息失败");
    }

    @Override
    public Result getMyMessages() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        List<Message> messages = messageMapper.selectByReceiverId(userId);
        return Result.success(messages);
    }

    @Override
    public Result getUnreadMessages() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        List<Message> messages = messageMapper.selectByReceiverIdAndStatus(userId, Message.STATUS_UNREAD);
        return Result.success(messages);
    }

    @Override
    public Result getUnreadCount() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Long count = messageMapper.countUnreadByUserId(userId);
        return Result.success(count);
    }

    @Override
    @Transactional
    public Result markAsRead(Long messageId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Message message = getById(messageId);
        if (message == null) {
            return Result.error(404, "消息不存在");
        }

        if (!message.getReceiverId().equals(userId)) {
            return Result.error(403, "无权操作此消息");
        }

        int updated = messageMapper.markAsRead(messageId);
        if (updated > 0) {
            return Result.success("标记已读成功", null);
        }
        return Result.error(500, "标记已读失败");
    }

    @Override
    @Transactional
    public Result markAllAsRead() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        int updated = messageMapper.markAllAsRead(userId);
        return Result.success("全部标记已读成功", updated);
    }

    @Override
    @Transactional
    public Result deleteMessage(Long messageId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Message message = getById(messageId);
        if (message == null) {
            return Result.error(404, "消息不存在");
        }

        if (!message.getReceiverId().equals(userId) && !message.getSenderId().equals(userId)) {
            return Result.error(403, "无权删除此消息");
        }

        boolean removed = removeById(messageId);
        if (removed) {
            return Result.success("删除消息成功", null);
        }
        return Result.error(500, "删除消息失败");
    }

    private Long getCurrentUserId() {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}
