
package ltd.shoping.mall.dao;

import ltd.shoping.mall.entity.shopingMallGoods;
import ltd.shoping.mall.entity.StockNumDTO;
import ltd.shoping.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface shopingMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(shopingMallGoods record);

    int insertSelective(shopingMallGoods record);

    shopingMallGoods selectByPrimaryKey(Long goodsId);

    shopingMallGoods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(shopingMallGoods record);

    int updateByPrimaryKeyWithBLOBs(shopingMallGoods record);

    int updateByPrimaryKey(shopingMallGoods record);

    List<shopingMallGoods> findshopingMallGoodsList(PageQueryUtil pageUtil);

    int getTotalshopingMallGoods(PageQueryUtil pageUtil);

    List<shopingMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<shopingMallGoods> findshopingMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalshopingMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("shopingMallGoodsList") List<shopingMallGoods> shopingMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int recoverStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}