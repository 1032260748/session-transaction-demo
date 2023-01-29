package com.example.db.demo;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.example.db.demo.entity.Product;
import com.example.db.demo.service.ProductService;
import com.example.db.demo.vo.ProductVo;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = RANDOM_PORT, properties = {
        "spring.profiles.active=local" })
public class ApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSave() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        Product product = productService.updatePrice(productVo);
        System.out.println(product.getPrice());
    }

    @Test
    public void testSaveAndFlush() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        Product product = productService.updatePriceFlush(productVo);
        System.out.println(product.getPrice());
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

        Product product = productService.getByCode("GOODS_020");
        System.out.println(product.getName());
    }

    @Test
    public void testSave2() {
        ProductVo productVo = new ProductVo();
        productVo.setName("书包");
        productVo.setDescription("书包");
        productVo.setCode("GOODS_020");
        productVo.setPrice(10);

        Product product = productService.save2(productVo);
        System.out.println(product.getPrice());
    }

}
