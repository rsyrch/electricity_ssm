package com.dao;

import com.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    // 查询所有商品
    List<Product> selectAllProduct();

    // 后台商品拼接条件查询
    List<Product> selectByNameAndId(@Param(value = "productId") Integer productId, @Param(value = "productName") String productName);

    List<Product> selectByNameAndCategoryId(@Param(value = "productName") String productName, @Param(value = "categoryIdList") List<Integer> categoryIdList);
}