package com.example.db.demo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.db.demo.entity.StudentEntity;
import com.example.db.demo.repository.StudentRepository;


public class EntityManagerTest extends ApplicationTests {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void testSave() {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName("test1");
        studentEntity.setNo("test1");
        studentRepository.saveAndFlush(studentEntity);
    }

    @Test
    public void test() {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            StudentEntity studentEntity = entityManager.find(StudentEntity.class, 1L);
            System.out.println(studentEntity.getName());

            studentEntity.setName("name4");

            entityManager.persist(studentEntity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        Assertions.assertTrue(true);
    }

    /**
     * 懒加载
     */
    @Test
    public void testGetReference() {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            StudentEntity studentEntity = entityManager.getReference(StudentEntity.class, 100L);
            System.out.println("test");
            System.out.println(studentEntity.getName());
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        Assertions.assertTrue(true);
    }

    /**
     * 懒加载
     */
    @Test
    public void testPersist() {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            StudentEntity studentEntity = new StudentEntity();
            studentEntity.setName("test1");
            studentEntity.setNo("test1");
            entityManager.persist(studentEntity);
            entityManager.flush();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        Assertions.assertTrue(true);
    }

    @Test
    public void testMerge() {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            StudentEntity studentEntity = new StudentEntity();
            studentEntity.setId(4L);
            studentEntity.setName("test1");
            studentEntity.setNo("test1");
            entityManager.merge(studentEntity);
            entityManager.flush();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        Assertions.assertTrue(true);
    }

}
