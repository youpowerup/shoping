
package ltd.shoping.mall.service;

import ltd.shoping.mall.controller.vo.shopingMallIndexCarouselVO;
import ltd.shoping.mall.entity.Carousel;
import ltd.shoping.mall.util.PageQueryUtil;
import ltd.shoping.mall.util.PageResult;

import java.util.List;

public interface shopingMallCarouselService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<shopingMallIndexCarouselVO> getCarouselsForIndex(int number);
}
