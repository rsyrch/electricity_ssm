package com.service;

import com.common.ServerResponse;
import com.vo.CartVo;

/**
 * @Date: 2018/6/20 17:57
 * @Description:
 */
public interface ICartService {
    ServerResponse<CartVo> addCatrs(Integer userId, Integer productId, Integer productNumber);

    ServerResponse<CartVo> updaetCarts(Integer userId, Integer productId, Integer productNumber);

    ServerResponse<CartVo> deleteCarts(Integer userId, String productIds);

    ServerResponse<CartVo> getCartList(Integer userId);
}
