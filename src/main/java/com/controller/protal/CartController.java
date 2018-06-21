package com.controller.protal;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.User;
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

    /**
     * Description: 产品添加到购物车
     * CreateDate: 2018/6/20 17:50
     *
    */
    @RequestMapping(value = "addCar.do")
    @ResponseBody
    public ServerResponse addCar(HttpSession session, Integer productId, Integer productNumber) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return null;
    }
}
