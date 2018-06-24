package com.service;

import com.common.ServerResponse;

import java.util.Map;

/**
 * @Date: 2018/6/23 23:02
 * @Description:
 */
public interface IOrderService {
    ServerResponse pay(Integer userId, String path, Long orderNumber);

    ServerResponse alipayCallback(Map<String, String> params);

    ServerResponse queryOrderStatus(Long orderNumber, Integer userId);
}
