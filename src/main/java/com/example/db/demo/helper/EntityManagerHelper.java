package com.example.db.demo.helper;

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
        Object obj = TransactionSynchronizationManager.getResource(this.entityManagerFactory);
        EntityManagerHolder entityManagerHolder = (EntityManagerHolder) obj;
        if (entityManagerHolder != null) {
            entityManagerHolder.getEntityManager().flush();
            entityManagerHolder.getEntityManager().clear();
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
