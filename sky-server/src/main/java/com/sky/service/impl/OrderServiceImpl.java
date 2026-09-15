package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(
                    MessageConstant.ADDRESS_BOOK_IS_NULL
            );
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null ||
                shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(
                    MessageConstant.SHOPPING_CART_IS_NULL
            );
        }
        Orders orders = new Orders();
        BeanUtils.copyProperties(
                ordersSubmitDTO,
                orders
        );
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(
                String.valueOf(System.currentTimeMillis())
        );
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        String address =
                addressBook.getProvinceName() +
                        addressBook.getCityName() +
                        addressBook.getDistrictName() +
                        addressBook.getDetail();
        orders.setAddress(address);
        orderMapper.insert(orders);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);
        shoppingCartMapper.delete(shoppingCart);
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(),
                new BigDecimal("0.01"),
                "苍穹外卖订单",
                user.getOpenid()
        );
        if (jsonObject.getString("code") != null &&
                "ORDERPAID".equals(
                        jsonObject.getString("code")
                )){
            throw new OrderBusinessException("该订单已支付");
        }
        OrderPaymentVO orderPaymentVO =
                jsonObject.toJavaObject(
                        OrderPaymentVO.class
                );
        orderPaymentVO.setPackageStr(
                jsonObject.getString("package")
        );
        return orderPaymentVO;
    }
    @Override
    public void paySuccess(String orderNumber) {
        Orders ordersDB = orderMapper.getByNumber(orderNumber);
        if (ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
        HashMap<String, Object> message = new HashMap<>();
        message.put("type","1");
        message.put("orderId",ordersDB.getId());
        message.put(
                "content",
                "订单号：" + orderNumber
        );
        String jsonMessage =
                JSON.toJSONString(message);
        webSocketServer.sendToAllClient(
                jsonMessage
        );
    }

    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum,pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);
        Page<Orders> ordersPage = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> orderVOList = new ArrayList<>();
        if (ordersPage != null && ordersPage.getTotal() > 0){
            for (Orders orders : ordersPage){
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(
                ordersPage.getTotal(),
                orderVOList
        );
    }

    @Override
    public OrderVO details(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(
                    MessageConstant.ORDER_NOT_FOUND
            );
        }
        List<OrderDetail> orderDetailList =
                orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(
                orders,
                orderVO
        );
        orderVO.setOrderDetailList(
                orderDetailList
        );
        return orderVO;
    }

    @Override
    public void userCancelById(Long id) throws Exception {
        Orders ordersDB = orderMapper.getById(id);
        if (ordersDB == null ||
                !ordersDB.getUserId().equals(
                        BaseContext.getCurrentId()
                )) {
            throw new OrderBusinessException(
                    MessageConstant.ORDER_NOT_FOUND
            );
        }
        if (ordersDB.getStatus() >
                Orders.TO_BE_CONFIRMED) {

            throw new OrderBusinessException(
                    MessageConstant.ORDER_STATUS_ERROR
            );
        }
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        if (ordersDB.getStatus().equals(
                Orders.TO_BE_CONFIRMED
        )) {
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(
                LocalDateTime.now()
        );
        orderMapper.update(orders);
    }
    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> shoppingCartList = orderDetailList.stream()
                .map(orderDetail -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
                    shoppingCart.setUserId(userId);
                    shoppingCart.setCreateTime(LocalDateTime.now());
                    return shoppingCart;
                })
                .collect(Collectors.toList());
        shoppingCartMapper.insertBatch(
                shoppingCartList
        );
    }
    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null || !orders.getUserId().equals(BaseContext.getCurrentId())){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        HashMap<String, Object> message = new HashMap<>();
        message.put("type","2");
        message.put("orderId",orders.getId());
        message.put("content","订单号： " + orders.getNumber());
        String jsonMessage = JSON.toJSONString(message);
        webSocketServer.sendToAllClient(jsonMessage);
    }


}
