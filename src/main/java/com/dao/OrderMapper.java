package com.dao;

import com.pojo.Order;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectOrderByUserIdAndOrderNumber(@Param(value = "userId") Integer userId, @Param(value = "orderNumber") Long orderNumber);

    Order selectByOrdeNumber(Long orderNumber);

}