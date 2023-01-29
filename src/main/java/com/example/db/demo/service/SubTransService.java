package com.example.db.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.Product;
import com.example.db.demo.repository.ProductRepository;


@Service
public class SubTransService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(rollbackFor = Exception.class)
    public void update(Product product) {
        product.setDescription("require");
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateNewTransaction(Product product) {
        product.setDescription("require_new");
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NEVER)
    public void updateNever(Product product) {
        product.setDescription("never");
        productRepository.saveAndFlush(product);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public void updateNested(Product product) {
        product.setDescription("nested");
        productRepository.saveAndFlush(product);
        throw new RuntimeException("error");
    }
}
