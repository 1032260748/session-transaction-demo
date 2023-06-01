package com.example.db.demo.service;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.ProductEntity;
import com.example.db.demo.helper.EntityManagerHelper;
import com.example.db.demo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class TransactionService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SubTransService subTransService;

    @Autowired
    private EntityManagerHelper entityManagerHelper;

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity saveProduct(ProductEntity product) {
        return productRepository.saveAndFlush(product);
    }

    public void callSelfParent(ProductEntity product) {
        try {
            callSelfSub(product);
        } catch (Exception ex) {
            log.error("callSelfSub error", ex);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void callSelfSub(ProductEntity product) {
        product.setDescription("callSelfSub");
        productRepository.saveAndFlush(product);
        throw new RuntimeException("error");
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
        sleep(3000);
        return productRepository.findTopByCode(code);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDescription(String code, String desc) {
        sleep(2000);
        ProductEntity product = productRepository.findTopByCode(code);
        product.setDescription(desc);
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public boolean readCommitReadTwice(String code) {
        ProductEntity oldProduct = productRepository.findTopByCode(code);
        String oldDesc = oldProduct.getDescription();

        sleep(6000);

        //clear缓存
        entityManagerHelper.clear();

        ProductEntity newProduct = productRepository.findTopByCode(code);
        String newDesc = newProduct.getDescription();
        return Objects.equals(oldDesc, newDesc);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public boolean repeatableReadTwice(String code) {
        ProductEntity oldProduct = productRepository.findTopByCode(code);
        String oldDesc = oldProduct.getDescription();
        log.info("old description:{}", oldDesc);

        sleep(6000);
        //clear缓存
        entityManagerHelper.clear();

        ProductEntity newProduct = productRepository.findTopByCode(code);
        String newDesc = newProduct.getDescription();
        log.info("new description:{}", newDesc);
        return Objects.equals(oldDesc, newDesc);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity insertNewPhantom(String desc) {
        sleep(3000);
        String productCode = Long.toString(System.currentTimeMillis());
        ProductEntity product = new ProductEntity();
        product.setName(productCode);
        product.setCode(productCode);
        product.setPrice(10);
        product.setDescription(desc);
        return productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public boolean repeatableReadPhantom(String description) {
        List<ProductEntity> oldList = productRepository.findAllByDescription(description);
        log.info("old list size:{}", oldList.size());

        sleep(6000);
        //clear缓存
        entityManagerHelper.clear();

        List<ProductEntity> newList = productRepository.findAllByDescription(description);
        log.info("new list size:{}", newList.size());
        return Objects.equals(oldList.size(), newList.size());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
