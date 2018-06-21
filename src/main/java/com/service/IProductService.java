package com.service;

import com.common.ServerResponse;
import com.github.pagehelper.PageInfo;
import com.pojo.Product;
import com.vo.ProductDetailVo;

/**
 * @Date: 2018/6/13 22:14
 * @Description:
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse editProductStatus(Integer productId, Integer status);

    ServerResponse<Object> manageGetProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNumber, int pageSize);

    ServerResponse<Object> productSearch(int pageNumber, int pageSize, Integer productId, String productName);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeyWordCategory(String productName, Integer categoryId, int pageNumber, int pageSize, String orderBy);
}
