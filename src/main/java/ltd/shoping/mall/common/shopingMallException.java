
package ltd.shoping.mall.common;

public class shopingMallException extends RuntimeException {

    public shopingMallException() {
    }

    public shopingMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new shopingMallException(message);
    }

}
