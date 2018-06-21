package com.service.impl;

import com.common.Const;
import com.common.ServerResponse;
import com.dao.CartMapper;
import com.pojo.Cart;
import com.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Date: 2018/6/20 17:59
 * @Description: 购物车模块
 */
@Service(value = "iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    public ServerResponse addCatrs(Integer userId, Integer productId, Integer productNumber) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if(cart == null) {
            // 购物车新增产品
            Cart newCart = new Cart();
            newCart.setQuantity(productNumber);
            newCart.setProductId(productId);
            newCart.setUserId(userId);
            newCart.setChecked(Const.Catr.CHECKED);
            int rowCount = cartMapper.insert(newCart);
        }
        else {
            // 更新购物车
            productNumber += cart.getQuantity();
            cart.setQuantity(productNumber);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return null;
    }

}
