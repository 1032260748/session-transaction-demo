package com.example.db.demo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.db.demo.entity.ProductEntity;
import com.example.db.demo.service.ProductService;
import com.example.db.demo.service.TransactionService;


public class TransactionServiceTest extends ApplicationTests {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductService productService;

    @Test
    public void callSelfTest() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("书包");
        productEntity.setDescription("书包");
        productEntity.setCode("GOODS_020");
        productEntity.setPrice(10);
        //还原数据
        productService.saveAndFlush(productEntity);

        try {
            transactionService.callSelfParent(productEntity);
            ProductEntity productInDb = productService.getByCode("GOODS_020");
            Assertions.assertEquals("callSelfSub", productInDb.getDescription());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            transactionService.callSelfSub(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void update() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("书包");
        productEntity.setDescription("书包");
        productEntity.setCode("GOODS_020");
        productEntity.setPrice(10);
        //还原数据
        productService.saveAndFlush(productEntity);

        try {
            //外层transaction抛异常，内层transaction修改production描述
            transactionService.updateRequire(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
            ProductEntity productInDb = productService.getByCode("GOODS_020");
            Assertions.assertNotEquals("require", productInDb.getDescription());
        }
    }

    @Test
    public void updateRequireSubError() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("书包");
        productEntity.setDescription("书包");
        productEntity.setCode("GOODS_020");
        productEntity.setPrice(10);
        //还原数据
        productService.saveAndFlush(productEntity);
        try {
            //外层transaction抛异常，内层transaction修改production描述
            transactionService.updateRequireSubError(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
            ProductEntity productInDb = productService.getByCode("GOODS_020");
            Assertions.assertNotEquals("updateRequireSubError", productInDb.getDescription());
        }
    }

    @Test
    public void updateRequireNew() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("书包");
        productEntity.setDescription("书包");
        productEntity.setCode("GOODS_020");
        productEntity.setPrice(10);
        //还原数据
        productService.saveAndFlush(productEntity);
        try {
            //外层transaction抛异常，内层transaction修改production描述
            transactionService.updateRequireNew(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
            ProductEntity productInDb = productService.getByCode("GOODS_020");
            Assertions.assertEquals("require_new", productInDb.getDescription());
        }
    }

    @Test
    public void updateRequireNewSubError() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("书包");
        productEntity.setDescription("书包");
        productEntity.setCode("GOODS_020");
        productEntity.setPrice(10);
        //还原数据
        productService.saveAndFlush(productEntity);
        try {
            //外层transaction抛异常，内层transaction修改production描述
            transactionService.updateRequireNewSubError(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
            ProductEntity productInDb = productService.getByCode("GOODS_020");
            Assertions.assertEquals("updateRequireNewSubError", productInDb.getDescription());
        }
    }

    @Test
    public void updateSubNever() {
        ProductEntity productVo = new ProductEntity();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        try {
            //外层有transaction，内层调用propagation = Propagation.NEVER
            transactionService.updateSubNever(productVo);
        } catch (Exception ex) {
            ProductEntity productInDb = productService.getByCode("GOODS_020");
            Assertions.assertNotEquals("never", productInDb.getDescription());
        }
    }

    @Test
    public void updateNestedJpa() {
        ProductEntity productVo = new ProductEntity();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);
        try {
            //jpa不支持这种类型
            transactionService.updateNested(productVo);
        } catch (Exception ex) {
            ProductEntity productInDb = productService.getByCode("GOODS_020");
            Assertions.assertNotEquals("nested_parent", productInDb.getDescription());
        }
    }

    @Test
    public void testReadDirty() {
        Runnable runnable = () -> transactionService.insertProduct("testReadDirty");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue(10));
        threadPoolExecutor.execute(runnable);
        ProductEntity product = transactionService.dirtyRead("testReadDirty");
        Assertions.assertNotNull(product);
    }

    @Test
    public void testReadCommitted() {
        Runnable runnable = () -> transactionService.insertProduct("testReadCommitted");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20,
                1, TimeUnit.MINUTES, new ArrayBlockingQueue(10));
        threadPoolExecutor.execute(runnable);
        ProductEntity product = transactionService.readCommitted("testReadCommitted");
        Assertions.assertNull(product);
    }

    @Test
    public void repeatableRead01() {
        String productCode = "repeatableRead01";

        ProductEntity product = new ProductEntity();
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

        ProductEntity product = new ProductEntity();
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
