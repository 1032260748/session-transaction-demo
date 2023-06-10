package com.example.db.demo.helper;

import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Component
public class EntityManagerHelper {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public void clear() {
        EntityManager entityManager = getEntityManager();
        if (Objects.nonNull(entityManager)) {
            entityManager.flush();
            entityManager.clear();
        }
    }

    public EntityManager getEntityManager() {
        Object obj = TransactionSynchronizationManager.getResource(this.entityManagerFactory);
        EntityManagerHolder entityManagerHolder = (EntityManagerHolder) obj;
        if (entityManagerHolder != null) {
            return entityManagerHolder.getEntityManager();
        }
        return null;
    }

}
