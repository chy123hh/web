package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.dto.request.CompleteProofRequest;
import org.example.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "接单、取消接单、上传凭证、确认完成等接口")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/take/{taskId}")
    @Operation(summary = "接单", description = "接受任务并创建订单")
    public Result takeOrder(
            @Parameter(description = "任务ID") @PathVariable Long taskId,
            @Parameter(description = "发布者ID") @RequestParam Long publisherId,
            @Parameter(description = "悬赏积分") @RequestParam Integer rewardPoints) {
        return orderService.takeOrder(taskId, publisherId, rewardPoints);
    }

    @PutMapping("/cancel/{orderId}")
    @Operation(summary = "取消接单", description = "取消已接的订单")
    public Result cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    @PostMapping("/complete-proof/{orderId}")
    @Operation(summary = "上传完成凭证", description = "上传任务完成的凭证图片")
    public Result uploadCompleteProof(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @RequestBody CompleteProofRequest request) {
        return orderService.uploadCompleteProof(orderId, request);
    }

    @PutMapping("/confirm/{orderId}")
    @Operation(summary = "确认完成", description = "发布者确认任务完成")
    public Result confirmOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        return orderService.confirmOrder(orderId);
    }

    @GetMapping("/my-taken")
    @Operation(summary = "我接的订单", description = "查询当前用户接单的所有订单")
    public Result listMyTakenOrders() {
        return orderService.listMyTakenOrders();
    }

    @GetMapping("/my-published")
    @Operation(summary = "我发布的订单", description = "查询当前用户发布的任务对应的接单记录")
    public Result listMyPublishedOrders() {
        return orderService.listMyPublishedOrders();
    }
}
