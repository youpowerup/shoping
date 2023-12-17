
package ltd.shoping.mall.dao;

import ltd.shoping.mall.entity.shopingMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface shopingMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(shopingMallShoppingCartItem record);

    int insertSelective(shopingMallShoppingCartItem record);

    shopingMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    shopingMallShoppingCartItem selectByUserIdAndGoodsId(@Param("shopingMallUserId") Long shopingMallUserId, @Param("goodsId") Long goodsId);

    List<shopingMallShoppingCartItem> selectByUserId(@Param("shopingMallUserId") Long shopingMallUserId, @Param("number") int number);

    int selectCountByUserId(Long shopingMallUserId);

    int updateByPrimaryKeySelective(shopingMallShoppingCartItem record);

    int updateByPrimaryKey(shopingMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}