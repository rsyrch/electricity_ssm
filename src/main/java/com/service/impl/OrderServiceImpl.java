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
    public ServerResponse addOrder(Integer userId) {
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
        if(!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        // 计算订单总价
        BigDecimal orderPrice = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList) {
            BigDecimalUtil.add(orderItem.getTotalPrice().doubleValue(), orderPrice.doubleValue());
        }

        // 生成订单
        Order order = new Order();

        return null;
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

}
