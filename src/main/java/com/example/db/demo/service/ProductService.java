package com.example.db.demo.service;

import java.util.Objects;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.Product;
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

    public Product getByCode(String code) {
        return productRepository.findTopByCode(code);
    }

    @Transactional(rollbackFor = Exception.class)
    public Product updatePrice(ProductVo productVo) {
        Product product = new Product();
        BeanUtils.copyProperties(productVo, product);

        //save
        Product saveResult = productRepository.save(product);

        System.out.println("开始更新");
        //price * 100
        Integer fenPrice = product.getPrice() * 100;
        productRepository.updatePriceByCode(product.getCode(), fenPrice);

        Product queryResult = productRepository.findTopByCode(saveResult.getCode());

        System.out.println(Objects.equals(queryResult, saveResult));

        return queryResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public Product updatePriceFlush(ProductVo productVo) {
        Product product = new Product();
        BeanUtils.copyProperties(productVo, product);

        Product saveResult = productRepository.save(product);
        System.out.println(Objects.equals(product, saveResult));

        System.out.println("开始更新");
        Integer fenPrice = product.getPrice() * 100;
        productRepository.updatePriceByCodeFlush(product.getCode(), fenPrice);

        Product queryResult = productRepository.findTopByCode(saveResult.getCode());

        System.out.println(Objects.equals(queryResult, saveResult));

        return queryResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public Product save2(ProductVo productVo) {
        Product product = new Product();
        BeanUtils.copyProperties(productVo, product);

        //save
        Product saveResult = productRepository.save(product);
        System.out.println("开始更新");

        //price * 100
        Integer fenPrice = product.getPrice() * 100;
        saveResult.setPrice(fenPrice);

        //SessionImpl merge
        productRepository.save(saveResult);

        Product queryResult = productRepository.findTopByCode(saveResult.getCode());
        System.out.println(Objects.equals(queryResult, saveResult));

        return queryResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAndDoOtherThing(ProductVo productVo) {
        Product product = new Product();
        BeanUtils.copyProperties(productVo, product);
        //save db
        productRepository.save(product);

        System.out.println("do other thing");
        stringRedisTemplate.opsForValue().set(productVo.getCode(), Integer.toString(product.getPrice()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveFlushAndDoOtherThing(ProductVo productVo) {
        Product product = new Product();
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
        Product product = productRepository.findTopByCode(code);
        product.setName("test1111");
    }

    @Transactional(rollbackFor = Exception.class)
    public void readAndUpdate(String code, String name) {
        //GOODS_020 test code
        productRepository.findTopByCode(code);
        Product product = productRepository.findTopByCode(code);
        product.setName(name);
    }

}
