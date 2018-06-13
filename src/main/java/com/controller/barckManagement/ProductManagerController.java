package com.controller.barckManagement;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.Product;
import com.pojo.User;
import com.service.IProductService;
import com.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @Date: 2018/6/13 22:12
 * @Description: 产品管理
 */
@Controller
@RequestMapping(value = "/manage/product/")
public class ProductManagerController {

    @Autowired
    IProductService iProductService;

    @Autowired
    IUserService iUserService;
    
    /**
     * Description: 产品保存
     * CreateDate: 2018/6/13 22:18
     * 
    */
    @RequestMapping(value = "productSave.do")
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            // 增加产品
            return iProductService.saveOrUpdateProduct(product);
        }
        else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }
    
    /**
     * Description: 修改产品状态
     * CreateDate: 2018/6/13 23:06
     * 
    */
    public ServerResponse editProductStatus(Integer productId, Integer status, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            return iProductService.editProductStatus(productId, status);
        }
        else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }
}
