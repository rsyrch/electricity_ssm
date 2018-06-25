package com.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @FunctionName:
 * @Name: luo chuan
 * @Date: 2018/6/9 16:53
 * @Description:
 */
public class Const {
    // 用户session key
    public static final String CURRENT_USER  = "userSessionName";

    public interface Roel {
        int ROLE_CUSTOMER = 0;  // 普通用户
        int ROLE_ADMIN = 1; // 管理员
    }

    // 产品列表动态排序字段
    public interface PorductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    // 购物车
    public interface Catr{
        int CHECKED = 1;    // 产品选中状态
        int NO_CHECKED = 0; // 产未选中状态
        String LIMIT_NUMBER_FAIL = "LIMIT_NUMBER_FAIL"; // 限制失败
        String LIMIT_NUMBER_SUCCESS = "LIMIT_NUMBER_SUCCESS"; // 限制成功
    }

    // 邮箱
    public static final String EMAIL = "email";

    // 用户姓名
    public static  final String USER_NAME = "userName";



    public enum ProductStatus {
        ON_SALE(1, "在线");
        private String value;
        private int code;
        ProductStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    // 订单状态
    public enum OrderStatus {
        CANCLE(0, "已取消"),
        NO_PAY(10, "未支付"),
        YES_PAY(20, "已支付"),
        YES_DELIVERY(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSE(60, "订单关闭");


        private int code;
        private String value;

        OrderStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static OrderStatus codeOf(int code) {
            for(OrderStatus orderStatus : values()) {
                if(orderStatus.getCode() == code) {
                    return orderStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    // 支付宝回调返回状态
    public interface AlipayCallbackStatus{
        // 交易创建，等待买家付款
        String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";

        // 未付款交易超时关闭，或支付完成后全额退款
        String TRADE_CLOSED = "TRADE_CLOSED";

        // 交易支付成功
        String TRADE_SUCCESS = "TRADE_SUCCESS";

        // 交易结束，不可退款
        String TRADE_FINISHED = "TRADE_FINISHED";

        // 返回给支付宝,停止回调
        String RESPONSE_SUCCESS = "success";

        // 支付失败
        String RESPONSE_FAILED = "failed";
    }

    // 支付类型
    public enum payPlatFrom {

        ALIPAY(1, "支付宝"),
        WXPAY(2, "微信");

        private int code;
        private String value;

        payPlatFrom(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    // 订单支付类型
    public enum paymentType {

        ONLINE_PAY(1, "在线支付");

        private int code;
        private String value;

        paymentType(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static paymentType codeOf(int code) {
            for(paymentType paymentType : values()) {
                if(paymentType.getCode() == code) {
                    return paymentType;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
