package com.example.db.demo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.db.demo.entity.Product;
import com.example.db.demo.service.TransactionService;


public class TransactionServiceTest extends ApplicationTests {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void update() {
        Product productVo = new Product();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        transactionService.update(productVo);
    }

    @Test
    public void updateRequireNew() {
        Product productVo = new Product();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        transactionService.updateRequireNew(productVo);
    }

    @Test
    public void updateSubNever() {
        Product productVo = new Product();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        transactionService.updateSubNever(productVo);
    }

    @Test
    public void updateNested() {
        Product productVo = new Product();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        transactionService.updateNested(productVo);
    }

    @Test
    public void testReadDirty() {
        Runnable runnable = () -> transactionService.insertProduct("testReadDirty");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue(10));
        threadPoolExecutor.execute(runnable);
        Product product = transactionService.dirtyRead("testReadDirty");
        Assertions.assertNotNull(product);
    }

    @Test
    public void testReadCommitted() {
        Runnable runnable = () -> transactionService.insertProduct("testReadCommitted");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue(10));
        threadPoolExecutor.execute(runnable);
        Product product = transactionService.readCommitted("testReadCommitted");
        Assertions.assertNull(product);
    }

    @Test
    public void repeatableRead01() {
        String productCode = "repeatableRead01";

        Product product = new Product();
        product.setName("product");
        product.setCode(productCode);
        product.setPrice(10);
        product.setDescription("old");
        transactionService.saveProduct(product);

        Runnable runnable = () -> transactionService.repeatableRead(productCode, "new");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue(10));
        threadPoolExecutor.execute(runnable);
        boolean result = transactionService.repeatableReadEqual(productCode);
        Assertions.assertFalse(result);
    }

    @Test
    public void repeatableRead02() {
        String productCode = "repeatableRead02";

        Product product = new Product();
        product.setName("product");
        product.setCode(productCode);
        product.setPrice(10);
        product.setDescription("old");
        transactionService.saveProduct(product);

        Runnable runnable = () -> transactionService.repeatableRead(productCode, "new");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue(10));
        threadPoolExecutor.execute(runnable);
        boolean result = transactionService.repeatableReadEqual2(productCode);
        Assertions.assertTrue(result);
    }

}
