package com.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Date: 2018/6/23 9:42
 * @Description: 购物车视图对象,属性中有多个CartProductVo
 */
public class CartVo {
    private List<CartProductVo> list;
    private BigDecimal cartTotalPrice;  // 购物车总价
    private Boolean allChecked; // 是否全部勾选
    private String imageHost; // 购物车图片

    public List<CartProductVo> getList() {
        return list;
    }

    public void setList(List<CartProductVo> list) {
        this.list = list;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
