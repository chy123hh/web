package org.example.service;

import org.example.common.dto.Result;
import org.example.dto.request.CompleteProofRequest;

public interface OrderService {

    /**
     * 接单
     */
    Result takeOrder(Long taskId, Long publisherId, Integer rewardPoints);

    /**
     * 取消接单
     */
    Result cancelOrder(Long orderId);

    /**
     * 上传完成凭证
     */
    Result uploadCompleteProof(Long orderId, CompleteProofRequest request);

    /**
     * 发布者确认完成
     */
    Result confirmOrder(Long orderId);

    /**
     * 查询我接的订单
     */
    Result listMyTakenOrders();

    /**
     * 查询我发布的订单对应的接单记录
     */
    Result listMyPublishedOrders();
}
