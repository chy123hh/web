package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.dto.request.CompleteProofRequest;
import org.example.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/take/{taskId}")
    public Result takeOrder(
            @PathVariable Long taskId,
            @RequestParam Long publisherId,
            @RequestParam Integer rewardPoints) {
        return orderService.takeOrder(taskId, publisherId, rewardPoints);
    }

    @PutMapping("/cancel/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    @PostMapping("/complete-proof/{orderId}")
    public Result uploadCompleteProof(
            @PathVariable Long orderId,
            @RequestBody CompleteProofRequest request) {
        return orderService.uploadCompleteProof(orderId, request);
    }

    @PutMapping("/confirm/{orderId}")
    public Result confirmOrder(@PathVariable Long orderId) {
        return orderService.confirmOrder(orderId);
    }

    @GetMapping("/my-taken")
    public Result listMyTakenOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return orderService.listMyTakenOrders(page, size);
    }

    @GetMapping("/my-published")
    public Result listMyPublishedOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return orderService.listMyPublishedOrders(page, size);
    }
}
