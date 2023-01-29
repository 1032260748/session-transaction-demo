package com.example.db.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, String>,
        JpaSpecificationExecutor<Product> {

    @Query("update product d set d.price = (:price) where d.code=(:code)")
    @Modifying
    @Transactional
    int updatePriceByCode(@Param("code") String code, @Param("price") Integer price);

    /**
     * ，这个时候用clearAutomatically=true,就会刷新hibernate的一级缓存了
     *
     * @param code
     * @param price
     * @return
     */
    @Query("update product d set d.price = (:price) where d.code=(:code)")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    int updatePriceByCodeFlush(@Param("code") String code, @Param("price") Integer price);

    Product findTopByCode(String code);

    @Transactional
    @Query("select d from product d  where d.code=(:code)")
    Product findTopByCodeFlush(@Param("code") String code);
}
