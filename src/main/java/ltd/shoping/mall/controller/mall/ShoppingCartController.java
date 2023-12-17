
package ltd.shoping.mall.controller.mall;

import ltd.shoping.mall.common.Constants;
import ltd.shoping.mall.common.shopingMallException;
import ltd.shoping.mall.common.ServiceResultEnum;
import ltd.shoping.mall.controller.vo.shopingMallShoppingCartItemVO;
import ltd.shoping.mall.controller.vo.shopingMallUserVO;
import ltd.shoping.mall.entity.shopingMallShoppingCartItem;
import ltd.shoping.mall.service.shopingMallShoppingCartService;
import ltd.shoping.mall.util.Result;
import ltd.shoping.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private shopingMallShoppingCartService shopingMallShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        shopingMallUserVO user = (shopingMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<shopingMallShoppingCartItemVO> myShoppingCartItems = shopingMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(shopingMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                shopingMallException.fail("购物项不能为空");
            }
            //总价
            for (shopingMallShoppingCartItemVO shopingMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += shopingMallShoppingCartItemVO.getGoodsCount() * shopingMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                shopingMallException.fail("购物项价格异常");
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveshopingMallShoppingCartItem(@RequestBody shopingMallShoppingCartItem shopingMallShoppingCartItem,
                                                 HttpSession httpSession) {
        shopingMallUserVO user = (shopingMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shopingMallShoppingCartItem.setUserId(user.getUserId());
        String saveResult = shopingMallShoppingCartService.saveshopingMallCartItem(shopingMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateshopingMallShoppingCartItem(@RequestBody shopingMallShoppingCartItem shopingMallShoppingCartItem,
                                                   HttpSession httpSession) {
        shopingMallUserVO user = (shopingMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shopingMallShoppingCartItem.setUserId(user.getUserId());
        String updateResult = shopingMallShoppingCartService.updateshopingMallCartItem(shopingMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{shopingMallShoppingCartItemId}")
    @ResponseBody
    public Result updateshopingMallShoppingCartItem(@PathVariable("shopingMallShoppingCartItemId") Long shopingMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        shopingMallUserVO user = (shopingMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = shopingMallShoppingCartService.deleteById(shopingMallShoppingCartItemId,user.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        shopingMallUserVO user = (shopingMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<shopingMallShoppingCartItemVO> myShoppingCartItems = shopingMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (shopingMallShoppingCartItemVO shopingMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += shopingMallShoppingCartItemVO.getGoodsCount() * shopingMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                shopingMallException.fail("购物项价格异常");
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
