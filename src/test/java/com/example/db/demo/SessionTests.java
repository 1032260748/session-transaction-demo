package com.example.db.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.db.demo.entity.ProductEntity;
import com.example.db.demo.service.ProductService;
import com.example.db.demo.vo.ProductVo;


public class SessionTests extends ApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    public void testFindTwice() {
        Assertions.assertTrue(productService.findTwice(PRODUCT_CODE));
    }

    @Test
    public void testFindTwiceNoTrans() {
        Assertions.assertFalse(productService.findTwiceNoTransaction(PRODUCT_CODE));
    }

    @Test
    public void testUpdatePriceNotChange() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode(PRODUCT_CODE);
        productVo.setPrice(10);

        ProductEntity product = productService.updatePrice(productVo);
        System.out.println(product.getPrice());
        Assertions.assertEquals(10, product.getPrice());
    }

    @Test
    public void testUpdatePriceChanged() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode(PRODUCT_CODE);
        productVo.setPrice(10);

        ProductEntity product = productService.updatePriceFlush(productVo);
        System.out.println(product.getPrice());
        Assertions.assertEquals(1000, product.getPrice());
    }

    @Test
    public void saveAndDoOtherThing() {
        Assertions.assertThrows(Exception.class, () -> {
                    ProductVo productVo = new ProductVo();
                    //db name非空
                    productVo.setName(null);
                    productVo.setDescription("手机");
                    productVo.setCode("002");
                    productVo.setPrice(5000);

                    productService.saveAndDoOtherThing(productVo);
                }
        );
    }

    @Test
    public void saveFlushAndDoOtherThing() {
        Assertions.assertThrows(Exception.class, () -> {
                    ProductVo productVo = new ProductVo();
                    //db name非空
                    productVo.setName(null);
                    productVo.setDescription("电脑");
                    productVo.setCode("003");
                    productVo.setPrice(15000);

                    productService.saveFlushAndDoOtherThing(productVo);
                }
        );

    }

    @Test
    public void testReadOnly() {
        //重置数据
        initProduct();
        String desc = "testReadOnly";
        productService.readOnlyAndUpdateDesc(PRODUCT_CODE, desc);
        ProductEntity product = productService.findById(PRODUCT_CODE);
        Assertions.assertNotEquals(desc, product.getDescription());
    }

    @Test
    public void testReadAndUpdate() {
        //重置数据
        initProduct();
        String desc = "testReadAndUpdate";
        productService.readAndUpdateDesc(PRODUCT_CODE, desc);
        ProductEntity product = productService.findById(PRODUCT_CODE);
        System.out.println(product.getName());
        Assertions.assertEquals(desc, product.getDescription());
    }

    @Test
    public void testSave2() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode(PRODUCT_CODE);
        productVo.setPrice(10);

        ProductEntity product = productService.save2(productVo);
        System.out.println(product.getPrice());
    }
}
