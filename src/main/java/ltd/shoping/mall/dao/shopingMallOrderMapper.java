
package ltd.shoping.mall.dao;

import ltd.shoping.mall.entity.shopingMallOrder;
import ltd.shoping.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface shopingMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(shopingMallOrder record);

    int insertSelective(shopingMallOrder record);

    shopingMallOrder selectByPrimaryKey(Long orderId);

    shopingMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(shopingMallOrder record);

    int updateByPrimaryKey(shopingMallOrder record);

    List<shopingMallOrder> findshopingMallOrderList(PageQueryUtil pageUtil);

    int getTotalshopingMallOrders(PageQueryUtil pageUtil);

    List<shopingMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}