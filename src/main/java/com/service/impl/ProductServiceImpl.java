package com.service.impl;

import com.common.ResponseCode;
import com.common.ServerResponse;
import com.dao.ProductMapper;
import com.pojo.Product;
import com.service.IProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Date: 2018/6/13 22:14
 * @Description:
 */
@Service(value = "iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;
    
    /**
     * Description: 新增或修改产品
     * CreateDate: 2018/6/13 22:33
    */
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product != null) {
            if(StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImages = product.getSubImages().split(",");
                if(subImages.length > 0) {
                    product.setMainImage(subImages[0]);
                }
            }

            // 更新产品
            if(product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount > 0) {
                    return ServerResponse.createBySuccess("更新成功");
                }
                else {
                    return ServerResponse.createByErrorMessage("更新失败");
                }
            }
            // 新增产品
            else {
                int rowCount = productMapper.insert(product);
                if(rowCount > 0) {
                    return ServerResponse.createBySuccess("新增成功");
                }
                else {
                    return ServerResponse.createByErrorMessage("新增失败");
                }
            }
        }
        else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }
    
    /**
     * Description: 编辑产品状态 1-在售 2-下架 3-删除
     * CreateDate: 2018/6/13 23:15
     * 
    */
    public ServerResponse editProductStatus(Integer productId, Integer status) {
        if(productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0) {
            return ServerResponse.createBySuccess("修改状态成功");
        }
        else {
            return ServerResponse.createByErrorMessage("修改状态失败");
        }
    }

}
