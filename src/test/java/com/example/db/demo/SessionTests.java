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
    public void testSave() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        ProductEntity product = productService.updatePrice(productVo);
        System.out.println(product.getPrice());
        Assertions.assertEquals(10, product.getPrice());
    }

    @Test
    public void testSaveAndFlush() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
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
                    productVo.setCode("GOODS_021");
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
                    productVo.setCode("GOODS_022");
                    productVo.setPrice(15000);

                    productService.saveFlushAndDoOtherThing(productVo);
                }
        );

    }

    @Test
    public void testReadOnly() {
        productService.readOnly("GOODS_020");
    }

    @Test
    public void testReadAndUpdate() {
        productService.readAndUpdate("GOODS_020", "123");

        ProductEntity product = productService.getByCode("GOODS_020");
        System.out.println(product.getName());
    }

    @Test
    public void testSave2() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        ProductEntity product = productService.save2(productVo);
        System.out.println(product.getPrice());
    }
}
