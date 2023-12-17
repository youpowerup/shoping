
package ltd.shoping.mall.service.impl;

import ltd.shoping.mall.common.Constants;
import ltd.shoping.mall.common.ServiceResultEnum;
import ltd.shoping.mall.controller.vo.shopingMallShoppingCartItemVO;
import ltd.shoping.mall.dao.shopingMallGoodsMapper;
import ltd.shoping.mall.dao.shopingMallShoppingCartItemMapper;
import ltd.shoping.mall.entity.shopingMallGoods;
import ltd.shoping.mall.entity.shopingMallShoppingCartItem;
import ltd.shoping.mall.service.shopingMallShoppingCartService;
import ltd.shoping.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class shopingMallShoppingCartServiceImpl implements shopingMallShoppingCartService {

    @Autowired
    private shopingMallShoppingCartItemMapper shopingMallShoppingCartItemMapper;

    @Autowired
    private shopingMallGoodsMapper shopingMallGoodsMapper;

    @Override
    public String saveshopingMallCartItem(shopingMallShoppingCartItem shopingMallShoppingCartItem) {
        shopingMallShoppingCartItem temp = shopingMallShoppingCartItemMapper.selectByUserIdAndGoodsId(shopingMallShoppingCartItem.getUserId(), shopingMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            temp.setGoodsCount(shopingMallShoppingCartItem.getGoodsCount());
            return updateshopingMallCartItem(temp);
        }
        shopingMallGoods shopingMallGoods = shopingMallGoodsMapper.selectByPrimaryKey(shopingMallShoppingCartItem.getGoodsId());
        //商品为空
        if (shopingMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shopingMallShoppingCartItemMapper.selectCountByUserId(shopingMallShoppingCartItem.getUserId()) + 1;
        //超出单个商品的最大数量
        if (shopingMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (shopingMallShoppingCartItemMapper.insertSelective(shopingMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateshopingMallCartItem(shopingMallShoppingCartItem shopingMallShoppingCartItem) {
        shopingMallShoppingCartItem shopingMallShoppingCartItemUpdate = shopingMallShoppingCartItemMapper.selectByPrimaryKey(shopingMallShoppingCartItem.getCartItemId());
        if (shopingMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (shopingMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //当前登录账号的userId与待修改的cartItem中userId不同，返回错误
        if (!shopingMallShoppingCartItemUpdate.getUserId().equals(shopingMallShoppingCartItem.getUserId())) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //数值相同，则不执行数据操作
        if (shopingMallShoppingCartItem.getGoodsCount().equals(shopingMallShoppingCartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        shopingMallShoppingCartItemUpdate.setGoodsCount(shopingMallShoppingCartItem.getGoodsCount());
        shopingMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (shopingMallShoppingCartItemMapper.updateByPrimaryKeySelective(shopingMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public shopingMallShoppingCartItem getshopingMallCartItemById(Long shopingMallShoppingCartItemId) {
        return shopingMallShoppingCartItemMapper.selectByPrimaryKey(shopingMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        shopingMallShoppingCartItem shopingMallShoppingCartItem = shopingMallShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (shopingMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(shopingMallShoppingCartItem.getUserId())) {
            return false;
        }
        return shopingMallShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<shopingMallShoppingCartItemVO> getMyShoppingCartItems(Long shopingMallUserId) {
        List<shopingMallShoppingCartItemVO> shopingMallShoppingCartItemVOS = new ArrayList<>();
        List<shopingMallShoppingCartItem> shopingMallShoppingCartItems = shopingMallShoppingCartItemMapper.selectByUserId(shopingMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(shopingMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> shopingMallGoodsIds = shopingMallShoppingCartItems.stream().map(shopingMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<shopingMallGoods> shopingMallGoods = shopingMallGoodsMapper.selectByPrimaryKeys(shopingMallGoodsIds);
            Map<Long, shopingMallGoods> shopingMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(shopingMallGoods)) {
                shopingMallGoodsMap = shopingMallGoods.stream().collect(Collectors.toMap(goods -> goods.getGoodsId(), Function.identity(), (entity1, entity2) -> entity1));
            }
            //shopingMallGoods::getGoodsId
            for (shopingMallShoppingCartItem shopingMallShoppingCartItem : shopingMallShoppingCartItems) {
                shopingMallShoppingCartItemVO shopingMallShoppingCartItemVO = new shopingMallShoppingCartItemVO();
                BeanUtil.copyProperties(shopingMallShoppingCartItem, shopingMallShoppingCartItemVO);
                if (shopingMallGoodsMap.containsKey(shopingMallShoppingCartItem.getGoodsId())) {
                    shopingMallGoods shopingMallGoodsTemp = shopingMallGoodsMap.get(shopingMallShoppingCartItem.getGoodsId());
                    shopingMallShoppingCartItemVO.setGoodsCoverImg(shopingMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = shopingMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    shopingMallShoppingCartItemVO.setGoodsName(goodsName);
                    shopingMallShoppingCartItemVO.setSellingPrice(shopingMallGoodsTemp.getSellingPrice());
                    shopingMallShoppingCartItemVOS.add(shopingMallShoppingCartItemVO);
                }
            }
        }
        return shopingMallShoppingCartItemVOS;
    }
}
