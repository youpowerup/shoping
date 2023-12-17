
package ltd.shoping.mall.service;

import ltd.shoping.mall.entity.shopingMallGoods;
import ltd.shoping.mall.util.PageQueryUtil;
import ltd.shoping.mall.util.PageResult;

import java.util.List;

public interface shopingMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getshopingMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveshopingMallGoods(shopingMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param shopingMallGoodsList
     * @return
     */
    void batchSaveshopingMallGoods(List<shopingMallGoods> shopingMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateshopingMallGoods(shopingMallGoods goods);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    shopingMallGoods getshopingMallGoodsById(Long id);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids,int sellStatus);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchshopingMallGoods(PageQueryUtil pageUtil);
}
