
package ltd.shoping.mall.controller.mall;

import ltd.shoping.mall.common.Constants;
import ltd.shoping.mall.common.IndexConfigTypeEnum;
import ltd.shoping.mall.common.shopingMallException;
import ltd.shoping.mall.controller.vo.shopingMallIndexCarouselVO;
import ltd.shoping.mall.controller.vo.shopingMallIndexCategoryVO;
import ltd.shoping.mall.controller.vo.shopingMallIndexConfigGoodsVO;
import ltd.shoping.mall.service.shopingMallCarouselService;
import ltd.shoping.mall.service.shopingMallCategoryService;
import ltd.shoping.mall.service.shopingMallIndexConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private shopingMallCarouselService shopingMallCarouselService;

    @Resource
    private shopingMallIndexConfigService shopingMallIndexConfigService;

    @Resource
    private shopingMallCategoryService shopingMallCategoryService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<shopingMallIndexCategoryVO> categories = shopingMallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            shopingMallException.fail("分类数据不完善");
        }
        List<shopingMallIndexCarouselVO> carousels = shopingMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<shopingMallIndexConfigGoodsVO> hotGoodses = shopingMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<shopingMallIndexConfigGoodsVO> newGoodses = shopingMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<shopingMallIndexConfigGoodsVO> recommendGoodses = shopingMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}
