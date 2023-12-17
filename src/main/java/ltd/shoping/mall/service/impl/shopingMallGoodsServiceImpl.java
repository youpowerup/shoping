
package ltd.shoping.mall.service.impl;

import ltd.shoping.mall.common.shopingMallCategoryLevelEnum;
import ltd.shoping.mall.common.shopingMallException;
import ltd.shoping.mall.common.ServiceResultEnum;
import ltd.shoping.mall.controller.vo.shopingMallSearchGoodsVO;
import ltd.shoping.mall.dao.GoodsCategoryMapper;
import ltd.shoping.mall.dao.shopingMallGoodsMapper;
import ltd.shoping.mall.entity.GoodsCategory;
import ltd.shoping.mall.entity.shopingMallGoods;
import ltd.shoping.mall.service.shopingMallGoodsService;
import ltd.shoping.mall.util.BeanUtil;
import ltd.shoping.mall.util.shopingMallUtils;
import ltd.shoping.mall.util.PageQueryUtil;
import ltd.shoping.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class shopingMallGoodsServiceImpl implements shopingMallGoodsService {

    @Autowired
    private shopingMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getshopingMallGoodsPage(PageQueryUtil pageUtil) {
        List<shopingMallGoods> goodsList = goodsMapper.findshopingMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalshopingMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveshopingMallGoods(shopingMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != shopingMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(shopingMallUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(shopingMallUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(shopingMallUtils.cleanString(goods.getTag()));
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveshopingMallGoods(List<shopingMallGoods> shopingMallGoodsList) {
        if (!CollectionUtils.isEmpty(shopingMallGoodsList)) {
            goodsMapper.batchInsert(shopingMallGoodsList);
        }
    }

    @Override
    public String updateshopingMallGoods(shopingMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != shopingMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        shopingMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        shopingMallGoods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(shopingMallUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(shopingMallUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(shopingMallUtils.cleanString(goods.getTag()));
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public shopingMallGoods getshopingMallGoodsById(Long id) {
        shopingMallGoods shopingMallGoods = goodsMapper.selectByPrimaryKey(id);
        if (shopingMallGoods == null) {
            shopingMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return shopingMallGoods;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchshopingMallGoods(PageQueryUtil pageUtil) {
        List<shopingMallGoods> goodsList = goodsMapper.findshopingMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalshopingMallGoodsBySearch(pageUtil);
        List<shopingMallSearchGoodsVO> shopingMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            shopingMallSearchGoodsVOS = BeanUtil.copyList(goodsList, shopingMallSearchGoodsVO.class);
            for (shopingMallSearchGoodsVO shopingMallSearchGoodsVO : shopingMallSearchGoodsVOS) {
                String goodsName = shopingMallSearchGoodsVO.getGoodsName();
                String goodsIntro = shopingMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    shopingMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    shopingMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(shopingMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
