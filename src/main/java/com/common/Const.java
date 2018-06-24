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

    public static final String EMAIL = "email";

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
}
