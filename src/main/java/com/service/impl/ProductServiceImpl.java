package com.service.impl;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.dao.CategoryMapper;
import com.dao.ProductMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pojo.Category;
import com.pojo.Product;
import com.service.ICategoryService;
import com.service.IProductService;
import com.util.DateTimeUtil;
import com.util.PropertiesUtil;
import com.vo.ProductDetailVo;
import com.vo.ProductListVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2018/6/13 22:14
 * @Description:
 */
@Service(value = "iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    // 平级调用,注入Service
    @Autowired
    ICategoryService iCategoryService;


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
    
    /**
     * Description: 后台获取商品详情
     * CreateDate: 2018/6/15 23:07
     * 
    */
    public ServerResponse<Object> manageGetProductDetail(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        }
        else {
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product == null) {
                return ServerResponse.createByErrorMessage("产品不存在或已经下架");
            }
            else {
                ProductDetailVo productDetailVo = productVoObject(product);
                return ServerResponse.createBySuccess(productDetailVo);
            }
        }
    }

    /**
     * Description: vo对象保存,对pojo对象的再次封装,便于数据在view层的展示
     * CreateDate: 2018/6/16 10:53
     * 
    */
    private ProductDetailVo productVoObject(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId()); // 主键
        productDetailVo.setSubtitle(product.getSubtitle()); // 副标题
        productDetailVo.setPrice(product.getPrice());   // 价格
        productDetailVo.setMainImage(product.getMainImage());   // 主图
        productDetailVo.setSubImages(product.getSubImages());   // 附图
        productDetailVo.setCategoryId(product.getCategoryId()); // 分类id
        productDetailVo.setDetail(product.getDetail()); // 详情描述
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus()); // 产品状态
        productDetailVo.setStock(product.getStock());   // 库存数量

        // ftp文件服务器的图片地址
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        // 分类信息
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null) {
            // 默认根节点，最高层级分类
            productDetailVo.setCategoryId(0);
        }
        else {
            productDetailVo.setCategoryId(category.getParentId());
        }

        // 时间格式化处理
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * Description: 获取商品列表
     * CreateDate: 2018/6/16 15:40
     * 
    */
    public ServerResponse<PageInfo> getProductList(int pageNumber, int pageSize) {
        // 使用分页插件: pagehelper
        // 开始分页
        PageHelper.startPage(pageNumber, pageSize);
        List<Product> productList = productMapper.selectAllProduct();
        List<ProductListVo> productListVoList = new ArrayList<>();
        PageInfo pageInfo = new PageInfo();
        if(productList.size() > 0) {
            for(Product product : productList) {
                // 对数据进行重新包装
                ProductListVo productListVo = productListVoListObject(product);
                productListVoList.add(productListVo);
            }
            pageInfo.setList(productListVoList);
        }
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListVo productListVoListObject(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return productListVo;
    }
    
    /**
     * Description: 商品搜索列表
     * CreateDate: 2018/6/16 16:14
     * 
    */
    public ServerResponse<Object> productSearch(int pageNumber, int pageSize, Integer productId, String productName) {
        PageHelper.startPage(pageNumber, pageSize);
        if(StringUtils.isNotBlank(productName)) {
            StringBuilder productNameSBr = new StringBuilder(productName);
            productNameSBr.append("%").append(productName).append("%");
            String name = new String(productNameSBr);
            List<Product> productList = productMapper.selectByNameAndId(productId, name);
            List<ProductListVo> productListVoList = new ArrayList<>();
            PageInfo pageInfo = new PageInfo();
            if(productList.size() > 0) {
                for(Product product : productList) {
                    // 对数据进行重新包装
                    ProductListVo productListVo = productListVoListObject(product);
                    productListVoList.add(productListVo);
                }
                pageInfo.setList(productListVoList);
            }
            return ServerResponse.createBySuccess(pageInfo);
        }
        return null;
    }
    
    /**
     * Description: 前台获取商品详情
     * CreateDate: 2018/6/19 22:11
     * 
    */
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        }
        else {
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product == null) {
                return ServerResponse.createByErrorMessage("产品不存在或已经下架");
            }
            else {
                if(product.getStatus() != Const.ProductStatus.ON_SALE.getCode()) {
                    return ServerResponse.createByErrorMessage("产品不在线");
                }
                ProductDetailVo productDetailVo = productVoObject(product);
                return ServerResponse.createBySuccess(productDetailVo);
            }
        }
    }
    
    /**
     * Description: 前台获取产品列表(根据产品名搜索，产品分类查找)
     * CreateDate: 2018/6/19 22:20
     * 
    */
    public ServerResponse<PageInfo> getProductByKeyWordCategory(String productName, Integer categoryId, int pageNumber,
                                                                int pageSize, String orderBy)
    {
        if(StringUtils.isBlank(productName) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if(categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(productName)) {
                PageHelper.startPage(pageNumber, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            // 获取当前分类和所有子分类id
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        // 搜索字段
        if(StringUtils.isNotBlank(productName)) {
            StringBuilder stringBuilder = new StringBuilder(productName);
            stringBuilder.append("%").append(productName).append("%");
        }
        else {
            productName = null;
        }
        // 开始分页
        PageHelper.startPage(pageNumber, pageSize);
        // 排序字段
        if(StringUtils.isNotBlank(orderBy)) {
            if(Const.PorductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                // 动态排序
                // orderBy参数：字段+空格+asc/desc
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryId(productName, categoryIdList.size() <= 0?null:categoryIdList);
        // 构建产品视图对象(vo)
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList) {
            ProductListVo productListVo = productListVoListObject(product);
            productListVoList.add(productListVo);
        }
        // 构建分页对象
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
