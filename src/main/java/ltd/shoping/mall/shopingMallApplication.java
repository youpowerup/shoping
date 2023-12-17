
package ltd.shoping.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("ltd.shoping.mall.dao")
@SpringBootApplication
public class shopingMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(shopingMallApplication.class, args);
    }
}
