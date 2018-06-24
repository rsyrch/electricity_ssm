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
import com.common.ServerResponse;
import com.dao.OrderItemMapper;
import com.dao.OrderMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pojo.Order;
import com.pojo.OrderItem;
import com.service.IOrderService;
import com.util.BigDecimalUtil;
import com.util.FTPUtil;
import com.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
}
