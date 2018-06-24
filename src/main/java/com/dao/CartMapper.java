package com.dao;

import com.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param(value = "userId") Integer userId, @Param(value = "productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    // 查找用户未勾选的产品
    int selectCartProductCheckedStatusByUserId(Integer userId);

    // 删除购物车中的产品
    int deleteProductByUserIdProductId(@Param(value = "userId") Integer userId, @Param(value = "productId") Integer productId);

    // 用户选中的产品
    List<Cart> selectCheckedCartByUserId(Integer userId);
}