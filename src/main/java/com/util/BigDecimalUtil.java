package com.util;

import java.math.BigDecimal;

/**
 * Description: 价格基本运算
 * CreateDate: 2018/6/23 10:06
 * 
*/
public class BigDecimalUtil {

    private BigDecimalUtil(){}


    /**
     * Description: 加
     * CreateDate: 2018/6/23 10:05
     *
    */
    public static BigDecimal add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    /**
     * Description: 减
     * CreateDate: 2018/6/23 10:05
     *
    */
    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    
    /**
     * Description: 乘
     * CreateDate: 2018/6/23 10:06
     * 
    */
    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }
    
    /**
     * Description: 除
     * CreateDate: 2018/6/23 10:06
     * 
    */
    public static BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入,保留2位小数

        //除不尽的情况
    }





}
