package com.example.db.demo;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.example.db.demo.entity.ProductEntity;
import com.example.db.demo.service.ProductService;
import com.example.db.demo.service.TransactionService;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = RANDOM_PORT, properties = {
        "spring.profiles.active=local" })
public class ApplicationTests {

    public static final String PRODUCT_CODE = "01";

    @Autowired
    protected TransactionService transactionService;

    @Autowired
    protected ProductService productService;

    @Test
    void contextLoads() {
    }

    /**
     * <p>
     * 还原数据
     * </p>
     *
     * @author jwang38@paypal.com
     * @date 2023-05-30 17:21:29
     */
    protected ProductEntity initProduct() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("书包");
        productEntity.setDescription("书包");
        productEntity.setCode(PRODUCT_CODE);
        productEntity.setPrice(10);
        //还原数据
        return productService.saveAndFlush(productEntity);
    }

    protected CompletableFuture runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

}
