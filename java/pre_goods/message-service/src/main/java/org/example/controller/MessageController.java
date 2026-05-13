package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.entity.Message;
import org.example.service.MessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public Result sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    @GetMapping("/list")
    public Result getMyMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return messageService.getMyMessages(page, size);
    }

    @GetMapping("/unread")
    public Result getUnreadMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return messageService.getUnreadMessages(page, size);
    }

    @GetMapping("/unread-count")
    public Result getUnreadCount() {
        return messageService.getUnreadCount();
    }

    @PutMapping("/read/{messageId}")
    public Result markAsRead(@PathVariable Long messageId) {
        return messageService.markAsRead(messageId);
    }

    @PutMapping("/read-all")
    public Result markAllAsRead() {
        return messageService.markAllAsRead();
    }

    @DeleteMapping("/{messageId}")
    public Result deleteMessage(@PathVariable Long messageId) {
        return messageService.deleteMessage(messageId);
    }
}
