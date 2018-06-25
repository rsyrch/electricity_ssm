package com.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Date: 2018/6/25 19:56
 * @Description: 订单视图对象
 */
public class OrderVo {
    // 订单编号
    private Long orderNumber;
    // 订单金额
    private BigDecimal orderPrice;
    // 付款类型
    private Integer paymentType;
    // 付款描述
    private String paymentTypeDescribe;
    // 运费
    private Integer postage;
    // 状态
    private Integer orderStatus;
    // 状态描述
    private  String orderStatuseDescribe;
    // 地址id
    private Integer addressId;
    // 收货人姓名
    private String receiveName;
    // 付款时间
    private String payTime;
    // 发货时间
    private String sendTime;
    // 结束时间
    private String endTime;
    // 创建时间
    private String createTime;
    // 关闭时间
    private String closeTime;
    // 订单明细
    private List<OrderItemVo> orderItemVoList;
    // 收获地址视图对象
    private AddressVo addressVo;

    private String imageHost;

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentTypeDescribe() {
        return paymentTypeDescribe;
    }

    public void setPaymentTypeDescribe(String paymentTypeDescribe) {
        this.paymentTypeDescribe = paymentTypeDescribe;
    }

    public Integer getPostage() {
        return postage;
    }

    public void setPostage(Integer postage) {
        this.postage = postage;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatuseDescribe() {
        return orderStatuseDescribe;
    }

    public void setOrderStatuseDescribe(String orderStatuseDescribe) {
        this.orderStatuseDescribe = orderStatuseDescribe;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public AddressVo getAddressVo() {
        return addressVo;
    }

    public void setAddressVo(AddressVo addressVo) {
        this.addressVo = addressVo;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
