
package ltd.shoping.mall.service.impl;

import ltd.shoping.mall.common.*;
import ltd.shoping.mall.controller.vo.*;
import ltd.shoping.mall.dao.shopingMallGoodsMapper;
import ltd.shoping.mall.dao.shopingMallOrderItemMapper;
import ltd.shoping.mall.dao.shopingMallOrderMapper;
import ltd.shoping.mall.dao.shopingMallShoppingCartItemMapper;
import ltd.shoping.mall.entity.shopingMallGoods;
import ltd.shoping.mall.entity.shopingMallOrder;
import ltd.shoping.mall.entity.shopingMallOrderItem;
import ltd.shoping.mall.entity.StockNumDTO;
import ltd.shoping.mall.service.shopingMallOrderService;
import ltd.shoping.mall.util.BeanUtil;
import ltd.shoping.mall.util.NumberUtil;
import ltd.shoping.mall.util.PageQueryUtil;
import ltd.shoping.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class shopingMallOrderServiceImpl implements shopingMallOrderService {

    @Autowired
    private shopingMallOrderMapper shopingMallOrderMapper;
    @Autowired
    private shopingMallOrderItemMapper shopingMallOrderItemMapper;
    @Autowired
    private shopingMallShoppingCartItemMapper shopingMallShoppingCartItemMapper;
    @Autowired
    private shopingMallGoodsMapper shopingMallGoodsMapper;

    @Override
    public PageResult getshopingMallOrdersPage(PageQueryUtil pageUtil) {
        List<shopingMallOrder> shopingMallOrders = shopingMallOrderMapper.findshopingMallOrderList(pageUtil);
        int total = shopingMallOrderMapper.getTotalshopingMallOrders(pageUtil);
        PageResult pageResult = new PageResult(shopingMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(shopingMallOrder shopingMallOrder) {
        shopingMallOrder temp = shopingMallOrderMapper.selectByPrimaryKey(shopingMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(shopingMallOrder.getTotalPrice());
            temp.setUserAddress(shopingMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (shopingMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<shopingMallOrder> orders = shopingMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (shopingMallOrder shopingMallOrder : orders) {
                if (shopingMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += shopingMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (shopingMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += shopingMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (shopingMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<shopingMallOrder> orders = shopingMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (shopingMallOrder shopingMallOrder : orders) {
                if (shopingMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += shopingMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (shopingMallOrder.getOrderStatus() != 1 && shopingMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += shopingMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (shopingMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<shopingMallOrder> orders = shopingMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (shopingMallOrder shopingMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (shopingMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += shopingMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (shopingMallOrder.getOrderStatus() == 4 || shopingMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += shopingMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间&&恢复库存
                if (shopingMallOrderMapper.closeOrder(Arrays.asList(ids), shopingMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0 && recoverStockNum(Arrays.asList(ids))) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(shopingMallUserVO user, List<shopingMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(shopingMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(shopingMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<shopingMallGoods> shopingMallGoods = shopingMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<shopingMallGoods> goodsListNotSelling = shopingMallGoods.stream()
                .filter(shopingMallGoodsTemp -> shopingMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            shopingMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, shopingMallGoods> shopingMallGoodsMap = shopingMallGoods.stream().collect(Collectors.toMap(goods -> goods.getGoodsId(), Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (shopingMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!shopingMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                shopingMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > shopingMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                shopingMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(shopingMallGoods)) {
            if (shopingMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = shopingMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    shopingMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                shopingMallOrder shopingMallOrder = new shopingMallOrder();
                shopingMallOrder.setOrderNo(orderNo);
                shopingMallOrder.setUserId(user.getUserId());
                shopingMallOrder.setUserAddress(user.getAddress());
                //总价
                for (shopingMallShoppingCartItemVO shopingMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += shopingMallShoppingCartItemVO.getGoodsCount() * shopingMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    shopingMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                shopingMallOrder.setTotalPrice(priceTotal);
                //订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                shopingMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (shopingMallOrderMapper.insertSelective(shopingMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<shopingMallOrderItem> shopingMallOrderItems = new ArrayList<>();
                    for (shopingMallShoppingCartItemVO shopingMallShoppingCartItemVO : myShoppingCartItems) {
                        shopingMallOrderItem shopingMallOrderItem = new shopingMallOrderItem();
                        //使用BeanUtil工具类将shopingMallShoppingCartItemVO中的属性复制到shopingMallOrderItem对象中
                        BeanUtil.copyProperties(shopingMallShoppingCartItemVO, shopingMallOrderItem);
                        //shopingMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        shopingMallOrderItem.setOrderId(shopingMallOrder.getOrderId());
                        shopingMallOrderItems.add(shopingMallOrderItem);
                    }
                    //保存至数据库
                    if (shopingMallOrderItemMapper.insertBatch(shopingMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    shopingMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                shopingMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            shopingMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        shopingMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public shopingMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        shopingMallOrder shopingMallOrder = shopingMallOrderMapper.selectByOrderNo(orderNo);
        if (shopingMallOrder == null) {
            shopingMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        //验证是否是当前userId下的订单，否则报错
        if (!userId.equals(shopingMallOrder.getUserId())) {
            shopingMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        List<shopingMallOrderItem> orderItems = shopingMallOrderItemMapper.selectByOrderId(shopingMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            shopingMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<shopingMallOrderItemVO> shopingMallOrderItemVOS = BeanUtil.copyList(orderItems, shopingMallOrderItemVO.class);
        shopingMallOrderDetailVO shopingMallOrderDetailVO = new shopingMallOrderDetailVO();
        BeanUtil.copyProperties(shopingMallOrder, shopingMallOrderDetailVO);
        shopingMallOrderDetailVO.setOrderStatusString(shopingMallOrderStatusEnum.getshopingMallOrderStatusEnumByStatus(shopingMallOrderDetailVO.getOrderStatus()).getName());
        shopingMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(shopingMallOrderDetailVO.getPayType()).getName());
        shopingMallOrderDetailVO.setshopingMallOrderItemVOS(shopingMallOrderItemVOS);
        return shopingMallOrderDetailVO;
    }

    @Override
    public shopingMallOrder getshopingMallOrderByOrderNo(String orderNo) {
        return shopingMallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = shopingMallOrderMapper.getTotalshopingMallOrders(pageUtil);
        List<shopingMallOrder> shopingMallOrders = shopingMallOrderMapper.findshopingMallOrderList(pageUtil);
        List<shopingMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(shopingMallOrders, shopingMallOrderListVO.class);
            //设置订单状态中文显示值
            for (shopingMallOrderListVO shopingMallOrderListVO : orderListVOS) {
                shopingMallOrderListVO.setOrderStatusString(shopingMallOrderStatusEnum.getshopingMallOrderStatusEnumByStatus(shopingMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = shopingMallOrders.stream().map(shopingMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<shopingMallOrderItem> orderItems = shopingMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<shopingMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(shopingMallOrderItem::getOrderId));
                for (shopingMallOrderListVO shopingMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(shopingMallOrderListVO.getOrderId())) {
                        List<shopingMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(shopingMallOrderListVO.getOrderId());
                        //将shopingMallOrderItem对象列表转换成shopingMallOrderItemVO对象列表
                        List<shopingMallOrderItemVO> shopingMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, shopingMallOrderItemVO.class);
                        shopingMallOrderListVO.setshopingMallOrderItemVOS(shopingMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String cancelOrder(String orderNo, Long userId) {
        shopingMallOrder shopingMallOrder = shopingMallOrderMapper.selectByOrderNo(orderNo);
        if (shopingMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(shopingMallOrder.getUserId())) {
                shopingMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (shopingMallOrder.getOrderStatus().intValue() == shopingMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || shopingMallOrder.getOrderStatus().intValue() == shopingMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || shopingMallOrder.getOrderStatus().intValue() == shopingMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || shopingMallOrder.getOrderStatus().intValue() == shopingMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //修改订单状态&&恢复库存
            if (shopingMallOrderMapper.closeOrder(Collections.singletonList(shopingMallOrder.getOrderId()), shopingMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0 && recoverStockNum(Collections.singletonList(shopingMallOrder.getOrderId()))) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        shopingMallOrder shopingMallOrder = shopingMallOrderMapper.selectByOrderNo(orderNo);
        if (shopingMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(shopingMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (shopingMallOrder.getOrderStatus().intValue() != shopingMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            shopingMallOrder.setOrderStatus((byte) shopingMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            shopingMallOrder.setUpdateTime(new Date());
            if (shopingMallOrderMapper.updateByPrimaryKeySelective(shopingMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        shopingMallOrder shopingMallOrder = shopingMallOrderMapper.selectByOrderNo(orderNo);
        if (shopingMallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (shopingMallOrder.getOrderStatus().intValue() != shopingMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            shopingMallOrder.setOrderStatus((byte) shopingMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            shopingMallOrder.setPayType((byte) payType);
            shopingMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            shopingMallOrder.setPayTime(new Date());
            shopingMallOrder.setUpdateTime(new Date());
            if (shopingMallOrderMapper.updateByPrimaryKeySelective(shopingMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<shopingMallOrderItemVO> getOrderItems(Long id) {
        shopingMallOrder shopingMallOrder = shopingMallOrderMapper.selectByPrimaryKey(id);
        if (shopingMallOrder != null) {
            List<shopingMallOrderItem> orderItems = shopingMallOrderItemMapper.selectByOrderId(shopingMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<shopingMallOrderItemVO> shopingMallOrderItemVOS = BeanUtil.copyList(orderItems, shopingMallOrderItemVO.class);
                return shopingMallOrderItemVOS;
            }
        }
        return null;
    }

    /**
     * 恢复库存
     * @param orderIds
     * @return
     */
    public Boolean recoverStockNum(List<Long> orderIds) {
        //查询对应的订单项
        List<shopingMallOrderItem> shopingMallOrderItems = shopingMallOrderItemMapper.selectByOrderIds(orderIds);
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(shopingMallOrderItems, StockNumDTO.class);
        //执行恢复库存的操作
        int updateStockNumResult = shopingMallGoodsMapper.recoverStockNum(stockNumDTOS);
        if (updateStockNumResult < 1) {
            shopingMallException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
            return false;
        } else {
            return true;
        }
    }
}