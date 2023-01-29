package com.example.db.demo.service;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.Product;
import com.example.db.demo.repository.ProductRepository;


@Service
public class TransactionService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SubTransService subTransService;

    @Transactional(rollbackFor = Exception.class)
    public Product saveProduct(Product product) {
        return productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Product product) {
        subTransService.update(product);
        throw new RuntimeException("error");
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRequireNew(Product product) {
        subTransService.updateNewTransaction(product);
        throw new RuntimeException("error");
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNested(Product product) {
        subTransService.updateNested(product);
        product.setDescription("nested_parent");
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSubNever(Product product) {
        subTransService.updateNever(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertProduct(String code) {
        Product productVo = new Product();
        productVo.setName("product");
        productVo.setDescription("product");
        productVo.setCode(code);
        productVo.setPrice(10);
        productRepository.saveAndFlush(productVo);
        sleep(6000);
        System.out.println("新增结束！");
        System.out.println(5 / 0);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Product dirtyRead(String code) {
        sleep(3000);
        return productRepository.findTopByCode(code);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Product readCommitted(String code) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return productRepository.findTopByCode(code);
    }

    @Transactional(rollbackFor = Exception.class)
    public void repeatableRead(String code, String desc) {
        sleep(2000);
        Product product = productRepository.findTopByCode(code);
        product.setDescription(desc);
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public boolean repeatableReadEqual(String code) {
        Product product = productRepository.findTopByCodeFlush(code);
        String oldDesc = product.getDescription();
        sleep(6000);
        //clear一级缓存
        productRepository.updatePriceByCodeFlush(code, product.getPrice());
        product = productRepository.findTopByCodeFlush(code);
        String newDesc = product.getDescription();
        return Objects.equals(oldDesc, newDesc);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public boolean repeatableReadEqual2(String code) {
        Product product = productRepository.findTopByCodeFlush(code);
        String oldDesc = product.getDescription();
        sleep(6000);
        //clear一级缓存
        productRepository.updatePriceByCodeFlush(code, product.getPrice());
        product = productRepository.findTopByCodeFlush(code);
        String newDesc = product.getDescription();
        return Objects.equals(oldDesc, newDesc);
    }

    private void sleep(long micros) {
        try {
            Thread.sleep(micros);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
