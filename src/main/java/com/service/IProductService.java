package com.service;

import com.common.ServerResponse;
import com.pojo.Product;

/**
 * @Date: 2018/6/13 22:14
 * @Description:
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse editProductStatus(Integer productId, Integer status);
}
