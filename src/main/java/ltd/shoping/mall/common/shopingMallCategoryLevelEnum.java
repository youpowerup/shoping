
package ltd.shoping.mall.common;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/shoping-ltd
 * @apiNote 分类级别
 */
public enum shopingMallCategoryLevelEnum {

    DEFAULT(0, "ERROR"),
    LEVEL_ONE(1, "一级分类"),
    LEVEL_TWO(2, "二级分类"),
    LEVEL_THREE(3, "三级分类");

    private int level;

    private String name;

    shopingMallCategoryLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static shopingMallCategoryLevelEnum getshopingMallOrderStatusEnumByLevel(int level) {
        for (shopingMallCategoryLevelEnum shopingMallCategoryLevelEnum : shopingMallCategoryLevelEnum.values()) {
            if (shopingMallCategoryLevelEnum.getLevel() == level) {
                return shopingMallCategoryLevelEnum;
            }
        }
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
