
package ltd.shoping.mall.service;

import ltd.shoping.mall.controller.vo.shopingMallShoppingCartItemVO;
import ltd.shoping.mall.entity.shopingMallShoppingCartItem;

import java.util.List;

public interface shopingMallShoppingCartService {

    /**
     * 保存商品至购物车中
     *
     * @param shopingMallShoppingCartItem
     * @return
     */
    String saveshopingMallCartItem(shopingMallShoppingCartItem shopingMallShoppingCartItem);

    /**
     * 修改购物车中的属性
     *
     * @param shopingMallShoppingCartItem
     * @return
     */
    String updateshopingMallCartItem(shopingMallShoppingCartItem shopingMallShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param shopingMallShoppingCartItemId
     * @return
     */
    shopingMallShoppingCartItem getshopingMallCartItemById(Long shopingMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     *
     * @param shoppingCartItemId
     * @param userId
     * @return
     */
    Boolean deleteById(Long shoppingCartItemId, Long userId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param shopingMallUserId
     * @return
     */
    List<shopingMallShoppingCartItemVO> getMyShoppingCartItems(Long shopingMallUserId);
}
