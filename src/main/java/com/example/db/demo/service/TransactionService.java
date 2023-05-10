package com.example.db.demo.service;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.ProductEntity;
import com.example.db.demo.repository.ProductRepository;


@Service
public class TransactionService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SubTransService subTransService;

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity saveProduct(ProductEntity product) {
        return productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRequire(ProductEntity product) {
        subTransService.updateRequire(product);
        throw new RuntimeException("error");
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRequireSubError(ProductEntity product) {
        try {
            subTransService.updateRequireError(product);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        product.setDescription("updateRequireSubError");
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRequireNew(ProductEntity product) {
        subTransService.updateNewTransaction(product);
        throw new RuntimeException("error");
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRequireNewSubError(ProductEntity product) {
        try {
            //捕获异常
            subTransService.updateNewTransactionError(product);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        product.setDescription("updateRequireNewSubError");
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNested(ProductEntity product) {
        try {
            subTransService.updateNested(product);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        product.setDescription("nested_parent");
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSubNever(ProductEntity product) {
        subTransService.updateNever(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertProduct(String code) {
        ProductEntity productVo = new ProductEntity();
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
    public ProductEntity dirtyRead(String code) {
        sleep(3000);
        return productRepository.findTopByCode(code);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProductEntity readCommitted(String code) {
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
        ProductEntity product = productRepository.findTopByCode(code);
        product.setDescription(desc);
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public boolean repeatableReadEqual(String code) {
        ProductEntity product = productRepository.findTopByCodeFlush(code);
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
        ProductEntity product = productRepository.findTopByCodeFlush(code);
        String oldDesc = product.getDescription();
        sleep(6000);
        //clear一级缓存
        productRepository.updatePriceByCodeFlush(code, product.getPrice());
        product = productRepository.findTopByCodeFlush(code);
        String newDesc = product.getDescription();
        return Objects.equals(oldDesc, newDesc);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
