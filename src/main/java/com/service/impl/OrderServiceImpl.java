package com.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.common.Const;
import com.common.ServerResponse;
import com.dao.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pojo.*;
import com.service.IOrderService;
import com.util.BigDecimalUtil;
import com.util.DateTimeUtil;
import com.util.FTPUtil;
import com.util.PropertiesUtil;
import com.vo.AddressVo;
import com.vo.OrderItemVo;
import com.vo.OrderVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Date: 2018/6/23 23:02
 * @Description:
 */
@Service(value = "iOrderService")
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public ServerResponse pay(Integer userId, String path, Long orderNumber) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectOrderByUserIdAndOrderNumber(userId, orderNumber);
        if(order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        resultMap.put("orderNumber", String.valueOf(order.getOrderNo()));
        // =================================================支付调用================================================

        // ----------------------------------参数传递----------------------------------

        // 唯一订单号
        String payOrderNumber = orderNumber.toString();

        // 订单标题(用户扫码之后可见内容)
        String orderTitle = "商城扫码支付,订单号：" + payOrderNumber;

        // 订单总金额(单位：元)
        String totalPrice = order.getPayment().toString();

        // 商户支付宝id(如果为空,默认为PID)
        String sellerId = "";

        // 订单描述
        String body = "购买商品共" + order.getPayment().toString() + "元";

        // 商户门店编号
        String storeId = "test_stor_id";

        // 商户操作员编号
        String operatorId = "test_operator_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时时间(单位：min)
        String timeoutExpress = "120m";

        // 商品明细列表
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNumberAndUserId(userId, order.getOrderNo());
        for(OrderItem orderItem : orderItemList) {
            // 商品id（使用国标）、商品名称、商品价格（单位为分）、商品数量
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }

        // ----------------------------------创建支付请求-------------------------------------

        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder();
        builder.setSubject(orderTitle)  // 订单标题
                .setTotalAmount(totalPrice) // 总计价格
                .setOutTradeNo(payOrderNumber)  // 唯一订单编号
                .setSellerId(sellerId).setBody(body)    // 订单描述
                .setOperatorId(operatorId)     // 商户操作员编号
                .setStoreId(storeId)    // 商户门店编号
                .setExtendParams(extendParams)  // 扩展参数
                .setTimeoutExpress(timeoutExpress)  // 超时时间
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))   // 回调地址
                .setGoodsDetailList(goodsDetailList);   // 商品详情列表

        AlipayTradeService alipayTradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);

        // ---------------------------------支付结果解析----------------------------------------
        switch (result.getTradeStatus()) {
            case SUCCESS: {
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 创建目录
                File folder = new File(path);
                if(!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 二维码完整路径
                String qrPath = String.format(path + "/" + "qr-%s.png", response.getOutTradeNo());

                // 创建二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                // 上传到FTP服务器

                // 文件名
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                // 目标文件目录和文件名
                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常", e);
                }
                logger.info("二维码path：" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            }

            case FAILED: {
                logger.error("支付宝预下单失败");
                return ServerResponse.createByErrorMessage("支付宝预下单失败");
            }

            case UNKNOWN: {
                logger.error("系统异常,预下单状态未知");
                return ServerResponse.createByErrorMessage("系统异常,预下单状态未知");
            }

            default: {
                logger.error("不支持的交易状态");
                return ServerResponse.createByErrorMessage("不支持的交易状态");
            }
        }
    }

    // 响应日志
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    /**
     * Description: 支付宝回调验证
     * CreateDate: 2018/6/24 13:03
     *
    */
    public ServerResponse alipayCallback(Map<String, String> params) {
        Long orderNumber = Long.parseLong(params.get("out_trade_no"));
        // 支付宝交易号
        String tradeNumber = params.get("trade_no");
        // 交易目前所处的状态
        String tradeStatus = params.get("trade_status");

        Order order = orderMapper.selectByOrdeNumber(orderNumber);
        if(order == null) {
            ServerResponse.createByErrorMessage("订单不存在");
        }
        if(order.getStatus() >= Const.OrderStatus.YES_PAY.getCode()) {
            return ServerResponse.createBySuccess("重复调用");
        }
        if(tradeStatus.equals(Const.AlipayCallbackStatus.TRADE_SUCCESS)) {
            // 交易成功,修改订单状态
            order.setStatus(Const.OrderStatus.YES_PAY.getCode());
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            orderMapper.updateByPrimaryKeySelective(order);
        }

        // 交易记录保存
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.payPlatFrom.ALIPAY.getCode());
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    /**
     * Description: 查询订单状态
     * CreateDate: 2018/6/24 16:00
     *
    */
    public ServerResponse queryOrderStatus(Long orderNumber, Integer userId) {
        Order order = orderMapper.selectOrderByUserIdAndOrderNumber(userId, orderNumber);
        if(order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        if(order.getStatus() >= Const.OrderStatus.YES_PAY.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    /**
     * Description: 保存订单
     * CreateDate: 2018/6/25 0:05
     *
    */
    public ServerResponse addOrder(Integer userId, Integer addressId) {
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
        if(!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        if(CollectionUtils.isEmpty(orderItemList)) {
            ServerResponse.createByErrorMessage("购物车为空");
        }
        // 计算订单总价
        BigDecimal orderPrice = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList) {
            orderPrice = BigDecimalUtil.add(orderItem.getTotalPrice().doubleValue(), orderPrice.doubleValue());
        }

        // 生成订单
        Order order = this.assembelOrder(userId, addressId, orderPrice);
        if(order == null) {
            ServerResponse.createByErrorMessage("创建订单失败");
        }

        for(OrderItem orderItem : orderItemList) {
            // 订单明细子表订单编号设置
            orderItem.setOrderNo(order.getOrderNo());
        }
        // 订单明细批量插入
        orderItemMapper.bathInsertOrderItem(orderItemList);
        // 更新库存
        this.updateStock(orderItemList);
        // 清空购物车中的购买产品
        this.clearCart(cartList);
        // 返回订单数据
        OrderVo orderVo = this.assemblyOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }
    
    /**
     * Description: 根据购物车生成订单明细数据
     * CreateDate: 2018/6/25 0:09
     * 
    */
    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("无结算产品");
        }
        for(Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            // 判断产品状态
            if(Const.ProductStatus.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.createByErrorMessage("产品: " + product.getName() + "已经下线, 不再出售");
            }
            // 校验库存
            if(cart.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("产品: " + product.getName() + "库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity()));
            orderItemList.add(orderItem);

        }
        return ServerResponse.createBySuccess(orderItemList);
    }
    
    /**
     * Description: 组装订单对象
     * CreateDate: 2018/6/25 10:39
     * 
    */
    private Order assembelOrder(Integer userId, Integer addressId, BigDecimal orderPrce) {
        Order order = new Order();
        Long orderNumber = this.generateOrderNumber();
        order.setOrderNo(orderNumber);
        order.setStatus(Const.OrderStatus.NO_PAY.getCode());
        // 运费
        order.setPostage(0);
        // 支付类型
        order.setPaymentType(Const.paymentType.ONLINE_PAY.getCode());
        // 订单金额
        order.setPayment(orderPrce);
        order.setUserId(userId);
        order.setShippingId(addressId);
        int rowCount = orderMapper.insert(order);
        if(rowCount > 0) {
            return order;
        }
        return null;
    }
    
    /**
     * Description: 生成订单编号
     * CreateDate: 2018/6/25 10:51
     * 
    */
    private Long generateOrderNumber() {
        Long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    /**
     * Description: 更新库存
     * CreateDate: 2018/6/25 19:44
     *
    */
    private void updateStock(List<OrderItem> orderItemList) {
        for(OrderItem orderItem : orderItemList) {
            Product product =  productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }
    
    /**
     * Description: 清除购物车中的已支付产品
     * CreateDate: 2018/6/25 19:51
     * 
    */
    private void clearCart(List<Cart> cartList) {
        for(Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * Description: 组装订单视图对象
     * CreateDate: 2018/6/25 22:18
     *
    */
    private OrderVo assemblyOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNumber(order.getOrderNo());
        orderVo.setOrderPrice(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDescribe(Const.paymentType.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setOrderStatus(order.getStatus());
        orderVo.setOrderStatuseDescribe(Const.OrderStatus.codeOf(order.getStatus()).getValue());
        orderVo.setAddressId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());

        if(shipping != null) {
            orderVo.setReceiveName(shipping.getReceiverName());
            orderVo.setAddressVo(this.assemblyAddressVo(shipping));
        }

        orderVo.setPayTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for(OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = this.assemblyOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }
    
    /**
     * Description: 组装地址视图对象
     * CreateDate: 2018/6/25 22:41
     * 
    */
    private AddressVo assemblyAddressVo(Shipping shipping) {
        AddressVo addressVo = new AddressVo();
        addressVo.setReceiverName(shipping.getReceiverName());
        addressVo.setReceiverPhone(shipping.getReceiverPhone());
        addressVo.setReceiverMobile(shipping.getReceiverMobile());
        addressVo.setReceiverProvince(shipping.getReceiverProvince());
        addressVo.setReceiverCity(shipping.getReceiverCity());
        addressVo.setReceiverDistrict(shipping.getReceiverDistrict());
        addressVo.setReceiverZip(shipping.getReceiverZip());
        return addressVo;
    }
    
    /**
     * Description: 组装订单明细试图对象
     * CreateDate: 2018/6/25 22:53
     * 
    */
    private OrderItemVo assemblyOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setOrderNumber(orderItem.getOrderNo());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
    
    /**
     * Description: 未付款取消订单
     * CreateDate: 2018/6/25 23:12
     * 
    */
    public ServerResponse cancleOrder(Integer userId, Long orderNumber) {

        Order order = orderMapper.selectOrderByUserIdAndOrderNumber(userId, orderNumber);
        if(order == null) {
            ServerResponse.createByErrorMessage("订单不存在");
        }
        if(order.getStatus() != Const.OrderStatus.NO_PAY.getCode()) {
            ServerResponse.createByErrorMessage("订单不满足取消的条件");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatus.CANCLE.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(rowCount > 0) {
            return ServerResponse.createBySuccess();
        }
        return null;
    }

}
