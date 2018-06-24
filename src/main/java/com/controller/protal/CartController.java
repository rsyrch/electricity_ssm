package com.controller.protal;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.ICartService;
import com.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Date: 2018/6/20 17:48
 * @Description: 购物车模块
 */
@Controller
@RequestMapping(value = "/car/")
public class CartController {

    @Autowired
    ICartService iCartService;

    /**
     * Description: 产品添加到购物车
     * CreateDate: 2018/6/20 17:50
     *
    */
    @RequestMapping(value = "addCart.do")
    @ResponseBody
    public ServerResponse<CartVo> addCart(HttpSession session, Integer productId, Integer productNumber) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return iCartService.addCatrs(user.getId(), productId, productNumber);
    }
    
    /**
     * Description: 更新购物车
     * CreateDate: 2018/6/23 11:26
     * 
    */
    @RequestMapping(value = "updateCart.do")
    @ResponseBody
    public ServerResponse<CartVo> updateCart(HttpSession session, Integer productId, Integer productNumber) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return iCartService.updaetCarts(user.getId(), productId, productNumber);
    }

    /**
     * Description: 删除购物车中的产品
     * CreateDate: 2018/6/23 11:38
     * 
    */
    @RequestMapping(value = "deleteCartProduct.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteCartProduct(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return iCartService.deleteCarts(user.getId(), productIds);
    }

    /**
     * Description: 用户获取购物车
     * CreateDate: 2018/6/23 11:59
     * 
    */
    @RequestMapping(value = "getCartList.do")
    @ResponseBody
    public ServerResponse<CartVo> getCartList(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }
        return iCartService.getCartList(user.getId());
    }


}
