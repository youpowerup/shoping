
package ltd.shoping.mall.service;

import ltd.shoping.mall.controller.vo.shopingMallOrderDetailVO;
import ltd.shoping.mall.controller.vo.shopingMallOrderItemVO;
import ltd.shoping.mall.controller.vo.shopingMallShoppingCartItemVO;
import ltd.shoping.mall.controller.vo.shopingMallUserVO;
import ltd.shoping.mall.entity.shopingMallOrder;
import ltd.shoping.mall.util.PageQueryUtil;
import ltd.shoping.mall.util.PageResult;

import java.util.List;

public interface shopingMallOrderService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getshopingMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param shopingMallOrder
     * @return
     */
    String updateOrderInfo(shopingMallOrder shopingMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 保存订单
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(shopingMallUserVO user, List<shopingMallShoppingCartItemVO> myShoppingCartItems);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    shopingMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    shopingMallOrder getshopingMallOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    List<shopingMallOrderItemVO> getOrderItems(Long id);
}
