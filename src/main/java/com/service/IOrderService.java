package com.service;

import com.common.ServerResponse;

/**
 * @Date: 2018/6/23 23:02
 * @Description:
 */
public interface IOrderService {
    ServerResponse pay(Integer userId, String path, Long orderNumber);
}
