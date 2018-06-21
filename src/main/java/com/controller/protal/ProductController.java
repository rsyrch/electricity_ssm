package com.controller.protal;

import com.common.ServerResponse;
import com.github.pagehelper.PageInfo;
import com.service.IProductService;
import com.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Date: 2018/6/18 22:49
 * @Description: 产品
 */
@Controller
@RequestMapping(value = "/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;
    
    
    /**
     * Description: 前台获取商品详情
     * CreateDate: 2018/6/19 12:42
     * required 默认为true
    */
    @RequestMapping(value = "getProductDetial.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        return iProductService.getProductDetail(productId);
    }
    
    /**
     * Description: 前台获取产品列表(根据产品名搜索，产品分类查找)
     * CreateDate: 2018/6/20 17:36
     * 
    */
    @RequestMapping(value = "")
    @ResponseBody
    public ServerResponse<PageInfo> productList(@RequestParam(value = "productName", required = false) String productName,
                                                @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                @RequestParam(value = "orderBy", defaultValue = "") String orderBy)
    {

        return iProductService.getProductByKeyWordCategory(productName, categoryId, pageNumber, pageSize, orderBy);
    }

}
