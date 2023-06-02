package com.example.db.demo.service;

import java.util.Map;
import java.util.Objects;
import javax.persistence.EntityManager;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.ProductEntity;
import com.example.db.demo.helper.EntityManagerHelper;
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

    @Autowired
    private EntityManagerHelper entityManagerHelper;

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public boolean findTwice(String code) {
        ProductEntity first = findById(code);
        ProductEntity second = findById(code);
        log.info("1: {}", first);
        log.info("2: {}", second);
        return first == second;
    }

    public boolean findTwiceNoTransaction(String code) {
        ProductEntity first = findById(code);
        ProductEntity second = findById(code);
        log.info("1: {}", first);
        log.info("2: {}", second);
        return first == second;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public boolean findCodeTwice(String code) {
        ProductEntity first = productRepository.findTopByCode(code);

        EntityManager entityManager = entityManagerHelper.getEntityManager();
        if (entityManager != null) {
            SessionImpl session = (SessionImpl) entityManager;
            Map<String, Object> map = session.getPersistenceContext().getEntitiesByKey();
            log.info("map size: {}", map.size());
            //entityManager.clear();
        }

        ProductEntity second = productRepository.findTopByCode(code);
        log.info("1: {}", first);
        log.info("2: {}", second);
        return first == second;
    }

    public ProductEntity findById(String code) {
        return productRepository.findById(code).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity updatePrice(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);

        //save
        ProductEntity saveResult = productRepository.save(product);

        log.info("开始更新");
        //price * 100
        Integer fenPrice = product.getPrice() * 100;
        productRepository.updatePriceByCode(product.getCode(), fenPrice);

        ProductEntity queryResult = findById(product.getCode());

        log.info("是否相等:{}", Objects.equals(queryResult, saveResult));

        return queryResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity updatePriceFlush(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);

        ProductEntity saveResult = productRepository.save(product);
        log.info("是否相等:{}", Objects.equals(product, saveResult));

        log.info("开始更新");
        Integer fenPrice = product.getPrice() * 100;
        productRepository.updatePriceByCodeFlush(product.getCode(), fenPrice);

        ProductEntity queryResult = findById(saveResult.getCode());

        log.info("是否相等:{}", Objects.equals(queryResult, saveResult));
        return queryResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity save2(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);

        //save
        ProductEntity saveResult = productRepository.save(product);
        log.info("开始更新");

        //price * 100
        Integer fenPrice = product.getPrice() * 100;
        saveResult.setPrice(fenPrice);

        //SessionImpl merge
        productRepository.save(saveResult);

        ProductEntity queryResult = findById(saveResult.getCode());
        log.info("queryResult,saveResult是否相等:{}", Objects.equals(queryResult, saveResult));

        return queryResult;
    }

    /**
     * 延迟提交
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAndDoOtherThing(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);
        //save db
        productRepository.save(product);

        log.info("do other thing");
        stringRedisTemplate.opsForValue().set(productVo.getCode(), Integer.toString(product.getPrice()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveFlushAndDoOtherThing(ProductVo productVo) {
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productVo, product);
        //save db
        productRepository.saveAndFlush(product);

        log.info("do other thing");
        stringRedisTemplate.opsForValue().set(productVo.getCode(), Integer.toString(product.getPrice()));
    }

    @Transactional(readOnly = true)
    public void readOnlyAndUpdateDesc(String code, String description) {
        //findById(code);
        ProductEntity product = findById(code);
        if (Objects.nonNull(product)) {
            product.setDescription(description);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void readAndUpdateDesc(String code, String description) {
        //findById(code);
        ProductEntity product = findById(code);
        if (Objects.nonNull(product)) {
            product.setDescription(description);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductEntity saveAndFlush(ProductEntity productEntity) {
        return productRepository.saveAndFlush(productEntity);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public boolean findTwiceReadCommit(String code) {
        ProductEntity first = findById(code);
        sleep(6000);
        //entityManagerHelper.clear();
        ProductEntity second = findById(code);
        log.info("1: {}", first.getDescription());
        log.info("2: {}", second.getDescription());
        return Objects.equals(first.getDescription(), second.getDescription());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateDescription(String code, String desc) {
        sleep(3000);
        ProductEntity product = productRepository.findTopByCode(code);
        product.setDescription(desc);
        productRepository.saveAndFlush(product);
    }

}
