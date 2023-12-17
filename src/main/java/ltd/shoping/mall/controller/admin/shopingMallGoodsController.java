
package ltd.shoping.mall.controller.admin;

import ltd.shoping.mall.common.Constants;
import ltd.shoping.mall.common.shopingMallCategoryLevelEnum;
import ltd.shoping.mall.common.shopingMallException;
import ltd.shoping.mall.common.ServiceResultEnum;
import ltd.shoping.mall.entity.GoodsCategory;
import ltd.shoping.mall.entity.shopingMallGoods;
import ltd.shoping.mall.service.shopingMallCategoryService;
import ltd.shoping.mall.service.shopingMallGoodsService;
import ltd.shoping.mall.util.PageQueryUtil;
import ltd.shoping.mall.util.Result;
import ltd.shoping.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Controller
@RequestMapping("/admin")
public class shopingMallGoodsController {

    @Resource
    private shopingMallGoodsService shopingMallGoodsService;
    @Resource
    private shopingMallCategoryService shopingMallCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "shoping_mall_goods");
        return "admin/shoping_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), shopingMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), shopingMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), shopingMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/shoping_mall_goods_edit";
            }
        }
        shopingMallException.fail("分类数据不完善");
        return null;
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        shopingMallGoods shopingMallGoods = shopingMallGoodsService.getshopingMallGoodsById(goodsId);
        if (shopingMallGoods.getGoodsCategoryId() > 0) {
            if (shopingMallGoods.getGoodsCategoryId() != null || shopingMallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = shopingMallCategoryService.getGoodsCategoryById(shopingMallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == shopingMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), shopingMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), shopingMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = shopingMallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), shopingMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = shopingMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (shopingMallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), shopingMallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), shopingMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = shopingMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), shopingMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", shopingMallGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/shoping_mall_goods_edit";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(shopingMallGoodsService.getshopingMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody shopingMallGoods shopingMallGoods) {
        if (!StringUtils.hasText(shopingMallGoods.getGoodsName())
                || !StringUtils.hasText(shopingMallGoods.getGoodsIntro())
                || !StringUtils.hasText(shopingMallGoods.getTag())
                || Objects.isNull(shopingMallGoods.getOriginalPrice())
                || Objects.isNull(shopingMallGoods.getGoodsCategoryId())
                || Objects.isNull(shopingMallGoods.getSellingPrice())
                || Objects.isNull(shopingMallGoods.getStockNum())
                || Objects.isNull(shopingMallGoods.getGoodsSellStatus())
                || !StringUtils.hasText(shopingMallGoods.getGoodsCoverImg())
                || !StringUtils.hasText(shopingMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = shopingMallGoodsService.saveshopingMallGoods(shopingMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody shopingMallGoods shopingMallGoods) {
        if (Objects.isNull(shopingMallGoods.getGoodsId())
                || !StringUtils.hasText(shopingMallGoods.getGoodsName())
                || !StringUtils.hasText(shopingMallGoods.getGoodsIntro())
                || !StringUtils.hasText(shopingMallGoods.getTag())
                || Objects.isNull(shopingMallGoods.getOriginalPrice())
                || Objects.isNull(shopingMallGoods.getSellingPrice())
                || Objects.isNull(shopingMallGoods.getGoodsCategoryId())
                || Objects.isNull(shopingMallGoods.getStockNum())
                || Objects.isNull(shopingMallGoods.getGoodsSellStatus())
                || !StringUtils.hasText(shopingMallGoods.getGoodsCoverImg())
                || !StringUtils.hasText(shopingMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = shopingMallGoodsService.updateshopingMallGoods(shopingMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        shopingMallGoods goods = shopingMallGoodsService.getshopingMallGoodsById(id);
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (shopingMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}