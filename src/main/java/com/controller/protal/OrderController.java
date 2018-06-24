package com.controller.protal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.google.common.collect.Maps;
import com.pojo.User;
import com.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * @Date: 2018/6/23 22:36
 * @Description: 订单管理
 */
@Controller
@RequestMapping(value = "/oder/")
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * Description: 支付
     * CreateDate: 2018/6/23 22:44
     * 
    */
    @RequestMapping(value = "pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNumber, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());

        }
        // 获取文件绝对路径
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(), path, orderNumber);
    }

    /**
     * Description: ----------------------------------支付宝回调函数--------------------------------------------------
     * CreateDate: 2018/6/24 11:56
     *
    */
    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> param = Maps.newHashMap();

        // 获取参数
        Map map = request.getParameterMap();
        for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next().toString();
            String[] values = (String[]) map.get(key);
            String value = "";
            for(int i = 0; i < values.length; i++) {
                // 参数拼接
                value += (i == values.length - 1) ? values[i] : values[i] + ",";
            }
            param.put(key, value);
        }
        logger.info("支付宝回调：sign:{}, trade_status:{}, 参数：{}", param.get("sign"),
                param.get("trade_status"), param.toString());

        // 回调验证,避免重复通知
        param.remove("sign_type");
        // 签名验证
        // 参数：参数map, 支付宝公钥, 字符集, 签名类型
        try {
            boolean alipaySignature = AlipaySignature.rsaCheckV2(param, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if(!alipaySignature) {
                return ServerResponse.createByErrorMessage("非法请求,验证失败");
            }
        } catch (AlipayApiException e) {
            logger.error("签名验证失败", e);
            e.printStackTrace();
        }

        // 订单逻辑处理
        ServerResponse serverResponse = iOrderService.alipayCallback(param);
        if(serverResponse.isSuccess()) {
            // 回调成功返回
            return Const.AlipayCallbackStatus.RESPONSE_SUCCESS;
        }
        // 回调失败返回
        return Const.AlipayCallbackStatus.RESPONSE_FAILED;
    }

    /**
     * Description: 查询订单是否支付
     * CreateDate: 2018/6/24 15:57
     *
    */
    @RequestMapping(value = "queryOrderStatus.do")
    @ResponseBody
    public ServerResponse queryOrderStatus(HttpSession session, Long orderNumber) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());

        }
        return iOrderService.queryOrderStatus(orderNumber, user.getId());

    }

    @RequestMapping(value = "addOrder.do")
    @ResponseBody
    public ServerResponse addOrder(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());

        }
        return null;
    }
}
