package com.example.db.demo.entity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;


/**
 * <p>
 * 支持二级缓存
 * 添加Cacheable
 * </p>
 *
 * @author jwang38@paypal.com
 * @version v1.0.0
 * @copyright Copyright(c) 2011-2020, Gopay All Rights Reserved.
 * @date 2023-05-25 14:00:13
 */
@Entity(name = "student")
@Setter
@Getter
@Builder
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String no;

    @Tolerate
    public StudentEntity() {
        //do nothing
    }

}
