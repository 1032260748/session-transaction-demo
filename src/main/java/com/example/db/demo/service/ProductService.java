package com.example.db.demo.service;

import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.ProductEntity;
import com.example.db.demo.repository.ProductRepository;
import com.example.db.demo.vo.ProductVo;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ProductService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductRepository productRepository;

    public ProductEntity getByCode(String code) {
        return productRepository.findTopByCode(code);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity updatePrice(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);

        //save
        ProductEntity saveResult = productRepository.save(product);

        System.out.println("开始更新");
        //price * 100
        Integer fenPrice = product.getPrice() * 100;
        productRepository.updatePriceByCode(product.getCode(), fenPrice);

        ProductEntity queryResult = productRepository.findTopByCode(saveResult.getCode());

        System.out.println(Objects.equals(queryResult, saveResult));

        return queryResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity updatePriceFlush(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);

        ProductEntity saveResult = productRepository.save(product);
        System.out.println(Objects.equals(product, saveResult));

        System.out.println("开始更新");
        Integer fenPrice = product.getPrice() * 100;
        productRepository.updatePriceByCodeFlush(product.getCode(), fenPrice);

        ProductEntity queryResult = productRepository.findTopByCode(saveResult.getCode());

        System.out.println(Objects.equals(queryResult, saveResult));

        return queryResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity save2(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);

        //save
        ProductEntity saveResult = productRepository.save(product);
        System.out.println("开始更新");

        //price * 100
        Integer fenPrice = product.getPrice() * 100;
        saveResult.setPrice(fenPrice);

        //SessionImpl merge
        productRepository.save(saveResult);

        ProductEntity queryResult = productRepository.findTopByCode(saveResult.getCode());
        System.out.println(Objects.equals(queryResult, saveResult));

        return queryResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAndDoOtherThing(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);
        //save db
        productRepository.save(product);

        System.out.println("do other thing");
        stringRedisTemplate.opsForValue().set(productVo.getCode(), Integer.toString(product.getPrice()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveFlushAndDoOtherThing(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);
        //save db
        productRepository.saveAndFlush(product);

        System.out.println("do other thing");
        stringRedisTemplate.opsForValue().set(productVo.getCode(), Integer.toString(product.getPrice()));
    }

    @Transactional(readOnly = true)
    public void readOnly(String code) {
        //GOODS_020 test code
        productRepository.findTopByCode(code);
        ProductEntity product = productRepository.findTopByCode(code);
        product.setName("test1111");
    }

    @Transactional(rollbackFor = Exception.class)
    public void readAndUpdate(String code, String name) {
        //GOODS_020 test code
        productRepository.findTopByCode(code);
        ProductEntity product = productRepository.findTopByCode(code);
        product.setName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity saveAndFlush(ProductEntity productEntity) {
        return productRepository.saveAndFlush(productEntity);
    }

}
