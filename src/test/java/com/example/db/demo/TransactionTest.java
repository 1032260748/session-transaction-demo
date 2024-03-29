package com.example.db.demo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import com.example.db.demo.entity.ProductEntity;


public class TransactionTest extends ApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testCallSelf() {
        try {
            ProductEntity productEntity = initProduct();
            transactionService.callSelfParent(productEntity);
            ProductEntity productInDb = productService.findById(PRODUCT_CODE);
            Assertions.assertEquals("callSelfSub", productInDb.getDescription());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            ProductEntity productEntity = initProduct();
            transactionService.callSelfSub(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ProductEntity productInDb = productService.findById(PRODUCT_CODE);
        Assertions.assertNotEquals("callSelfSub", productInDb.getDescription());
    }

    @Test
    public void testTransactionRequire() {
        try {
            //还原数据
            ProductEntity productEntity = initProduct();
            //外层transaction抛异常，内层transaction修改production描述
            transactionService.updateRequire(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
            ProductEntity productInDb = productService.findById(PRODUCT_CODE);
            Assertions.assertNotEquals("require", productInDb.getDescription());
        }
    }

    @Test
    public void testRequireSubError() {
        try {
            //还原数据
            ProductEntity productEntity = initProduct();
            //外层transaction抛异常，内层transaction修改production描述
            transactionService.updateRequireSubError(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
            ProductEntity productInDb = productService.findById(PRODUCT_CODE);
            Assertions.assertNotEquals("updateRequireSubError", productInDb.getDescription());
        }
    }

    @Test
    public void updateRequireNew() {
        try {
            //还原数据
            ProductEntity productEntity = initProduct();
            //外层transaction抛异常，内层transaction修改production描述
            transactionService.updateRequireNew(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
            ProductEntity productInDb = productService.findById(PRODUCT_CODE);
            Assertions.assertEquals("require_new", productInDb.getDescription());
        }
    }

    @Test
    public void updateRequireNewSubError() {
        try {
            //还原数据
            ProductEntity productEntity = initProduct();
            //外层transaction抛异常，内层transaction修改production描述
            transactionService.updateRequireNewSubError(productEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
            ProductEntity productInDb = productService.findById(PRODUCT_CODE);
            Assertions.assertEquals("updateRequireNewSubError", productInDb.getDescription());
        }
    }

    @Test
    public void updateSubNever() {
        try {
            //还原数据
            ProductEntity productEntity = initProduct();
            //外层有transaction，内层调用propagation = Propagation.NEVER
            transactionService.updateSubNever(productEntity);
        } catch (Exception ex) {
            ProductEntity productInDb = productService.findById(PRODUCT_CODE);
            Assertions.assertNotEquals("never", productInDb.getDescription());
        }
    }

    /**
     * <p>
     * 嵌套事务测试
     * </p>
     *
     * @author jwang38@paypal.com
     * @date 2023-05-31 09:47:28
     */
    @Test
    public void updateNestedJpa() {
        try {
            ProductEntity productEntity = initProduct();
            //jpa不支持这种类型
            transactionService.updateNested(productEntity);
        } catch (Exception ex) {
            ProductEntity productInDb = productService.findById(PRODUCT_CODE);
            Assertions.assertNotEquals("nested_parent", productInDb.getDescription());
        }
    }

    @Test
    public void testReadDirty() {
        CompletableFuture future = runAsync(() -> {
                    transactionService.insertProduct("testReadDirty");
                }
        );
        ProductEntity product = transactionService.dirtyRead("testReadDirty");
        CompletableFuture.allOf(future).join();
        Assertions.assertNotNull(product);
    }

    @Test
    public void testReadCommitted() {
        CompletableFuture future = runAsync(() -> {
                    transactionService.insertProduct("testReadCommitted");
                }
        );
        ProductEntity product = transactionService.readCommitted("testReadCommitted");
        CompletableFuture.allOf(future).join();
        Assertions.assertNull(product);
    }

    @Test
    public void testReadCommitRepeatRead() {
        String productCode = "testReadCommitRepeatRead";

        ProductEntity product = new ProductEntity();
        product.setName("product");
        product.setCode(productCode);
        product.setPrice(10);
        product.setDescription("old");
        transactionService.saveProduct(product);

        CompletableFuture future = runAsync(() -> {
                    transactionService.updateDescription(productCode, "new");
                }
        );
        boolean result = transactionService.readCommitReadTwice(productCode);

        CompletableFuture.allOf(future).join();

        Assertions.assertFalse(result);
    }

    @Test
    public void testRepeatableRead() {
        String productCode = "testRepeatableRead";

        ProductEntity product = new ProductEntity();
        product.setName("product");
        product.setCode(productCode);
        product.setPrice(10);
        product.setDescription("old");
        transactionService.saveProduct(product);

        Runnable runnable = () -> transactionService.updateDescription(productCode, "new");

        CompletableFuture future = runAsync(runnable);

        boolean result = transactionService.repeatableReadTwice(productCode);

        CompletableFuture.allOf(future).join();
        Assertions.assertTrue(result);
    }

    /**
     * <p>
     * 幻读测试
     * </p>
     *
     * @author jwang38@paypal.com
     * @date 2023-05-30 17:58:07
     */
    @Test
    public void testRepeatableReadPhantom() {
        String desc = "testRepeatableReadPhantom";
        String productCode = Long.toString(System.currentTimeMillis());

        ProductEntity product = new ProductEntity();
        product.setName(productCode);
        product.setCode(productCode);
        product.setPrice(10);
        product.setDescription(desc);
        transactionService.saveProduct(product);

        Runnable runnable = () -> transactionService.insertNewPhantom(desc);

        CompletableFuture future = runAsync(runnable);

        boolean result = transactionService.repeatableReadPhantom(desc);

        CompletableFuture.allOf(future).join();
        Assertions.assertTrue(result);
    }

    @Test
    public void testTimeout() {
        try {
            transactionService.timeoutTest("desc");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * JDBC事务
     *
     * @throws SQLException
     */
    @Test
    public void testTransaction() throws SQLException {
        //获取数据库连接
        Connection conn = null;
        Statement statement = null;
        try {
            conn = this.dataSource.getConnection();
            conn.setAutoCommit(false);
            //编写sql语句
            String sql = "update product set description='02' where code='02'";
            //创建Statement
            statement = conn.createStatement();
            //执行sql语句
            int count = statement.executeUpdate(sql);
            System.out.println("执行sql成功，一共影响了" + count + "条数据");
            conn.commit();
        } catch (Exception ex) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

    }

}
