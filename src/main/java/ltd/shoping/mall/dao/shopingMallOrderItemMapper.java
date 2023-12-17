
package ltd.shoping.mall.dao;

import ltd.shoping.mall.entity.shopingMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface shopingMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(shopingMallOrderItem record);

    int insertSelective(shopingMallOrderItem record);

    shopingMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<shopingMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<shopingMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<shopingMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(shopingMallOrderItem record);

    int updateByPrimaryKey(shopingMallOrderItem record);
}