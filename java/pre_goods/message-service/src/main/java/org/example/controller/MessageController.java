package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.entity.Message;
import org.example.service.MessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Tag(name = "消息管理", description = "发送消息、查询消息、标记已读等接口")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "向指定用户发送消息")
    public Result sendMessage(
            @Parameter(description = "消息内容") @RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    @GetMapping("/list")
    @Operation(summary = "获取我的消息", description = "查询当前用户接收的所有消息")
    public Result getMyMessages() {
        return messageService.getMyMessages();
    }

    @GetMapping("/unread")
    @Operation(summary = "获取未读消息", description = "查询当前用户的未读消息列表")
    public Result getUnreadMessages() {
        return messageService.getUnreadMessages();
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获取未读消息数量", description = "查询当前用户的未读消息数量")
    public Result getUnreadCount() {
        return messageService.getUnreadCount();
    }

    @PutMapping("/read/{messageId}")
    @Operation(summary = "标记已读", description = "将指定消息标记为已读")
    public Result markAsRead(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        return messageService.markAsRead(messageId);
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部标记已读", description = "将当前用户的所有未读消息标记为已读")
    public Result markAllAsRead() {
        return messageService.markAllAsRead();
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除消息", description = "删除指定消息")
    public Result deleteMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        return messageService.deleteMessage(messageId);
    }
}
