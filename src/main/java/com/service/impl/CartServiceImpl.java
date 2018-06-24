package com.service.impl;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.dao.CartMapper;
import com.dao.ProductMapper;
import com.google.common.collect.Lists;
import com.pojo.Cart;
import com.pojo.Product;
import com.service.ICartService;
import com.util.BigDecimalUtil;
import com.util.PropertiesUtil;
import com.vo.CartProductVo;
import com.vo.CartVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Date: 2018/6/20 17:59
 * @Description: 购物车模块
 */
@Service(value = "iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;
    
    
    /**
     * Description: 添加到购物车
     * CreateDate: 2018/6/23 9:32
     * 
    */
    public ServerResponse<CartVo> addCatrs(Integer userId, Integer productId, Integer productNumber) {
        if(productId == null || productNumber == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
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
        CartVo cartVo = this.createCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }
    
    /**
     * Description: 封装购物车视图对象
     * CreateDate: 2018/6/23 9:48
     * 
    */
    private CartVo createCartVo(Integer userId) {
        CartVo cartVo = new CartVo();
        // 购物车产品list
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        // 初始化购物车总价
        // 用String构造器避免精度丢失问题
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)) {
            for(Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    // 判断库存
                    int byCount = 0;
                    if(product.getStock() >= cart.getQuantity()) {
                        byCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Catr.LIMIT_NUMBER_SUCCESS);
                    }
                    else {
                        byCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Catr.LIMIT_NUMBER_FAIL);
                        // 更新购物车的有效库存
                        Cart newCart = new Cart();
                        newCart.setQuantity(byCount);
                        newCart.setId(cart.getId());
                        cartMapper.updateByPrimaryKeySelective(newCart);
                    }
                    cartProductVo.setQuantity(byCount);
                    // 产品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                // 产品是否已经勾选,若勾选,购物车总价增加
                if(cartProductVo.getProductChecked() == Const.Catr.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                // 产品添加到购物车产品list
                cartProductVoList.add(cartProductVo);
            }

        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setList(cartProductVoList);
        cartVo.setAllChecked(this.allProductChecked(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**
     * Description: 判断用户的购物车中的产品是否全部选中
     * CreateDate: 2018/6/23 10:53
     * 
    */
    private Boolean allProductChecked(Integer userId) {
        if(userId == null) {
            return false;
        }
        int count = cartMapper.selectCartProductCheckedStatusByUserId(userId);
        if(count == 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Description: 更新购物车
     * CreateDate: 2018/6/23 11:32
     * 
    */
    public ServerResponse<CartVo> updaetCarts(Integer userId, Integer productId, Integer productNumber) {
        if(productId == null || productNumber == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if(cart != null) {
            cart.setQuantity(productNumber);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        CartVo cartVo = this.createCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * Description: 删除购物车中的产品
     * CreateDate: 2018/6/23 11:39
     * 
    */
    public ServerResponse<CartVo> deleteCarts(Integer userId, String productIds) {
        if(StringUtils.isBlank(productIds)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        String[] productIdArray = productIds.split(",");
        if(productIdArray == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        for(String productId : productIdArray) {
            Integer productIdInt = Integer.parseInt(productId);
            cartMapper.deleteProductByUserIdProductId(userId, productIdInt);
        }
        CartVo cartVo = this.createCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * Description: 用户获取购物车
     * CreateDate: 2018/6/23 12:01
     * 
    */
    public ServerResponse<CartVo> getCartList(Integer userId) {
        CartVo cartVo = this.createCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * Description: 全选，全反选，单独选，单独反选
     * CreateDate: 2018/6/23 12:07
     * 
    */
    public ServerResponse<CartVo> selectList(Integer userId) {
        CartVo cartVo = this.createCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }
}
