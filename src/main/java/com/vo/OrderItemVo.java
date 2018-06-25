package com.vo;

import java.math.BigDecimal;

/**
 * @Date: 2018/6/25 20:09
 * @Description: 订单明细试图对象
 */
public class OrderItemVo {
    // 订单编号
    private Long orderNumber;
    // 产品id
    private Integer productId;
    // 产品名字
    private String productName;
    // 产品主图
    private String productImage;
    // 产品单价
    private BigDecimal currentUnitPrice;
    // 数量
    private Integer quantity;
    // 总价
    private BigDecimal totalPrice;
    // 创建时间
    private String createTime;

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
