package com.controller.protal;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Date: 2018/6/23 22:36
 * @Description: 订单管理
 */
@Controller
@RequestMapping(value = "/oder/")
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    /**
     * Description: 支付
     * CreateDate: 2018/6/23 22:44
     * 
    */
    public ServerResponse pay(HttpSession session, Long orderNumber, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());

        }
        // 获取文件绝对路径
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(), path, orderNumber);
    }
}
