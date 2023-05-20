package com.example.db.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.db.demo.entity.StudentEntity;
import com.example.db.demo.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class StudentService {

    @Autowired
    private StudentRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public void init() {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setId(1L);
        studentEntity.setName("name1");
        studentEntity.setNo("no1");
        repository.saveAndFlush(studentEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean findTwice(Long id) {
        StudentEntity first = repository.findById(id).orElse(null);
        StudentEntity second = repository.findById(id).orElse(null);
        log.info("是否相等:{}", first != null && first == second);
        return first != null && first == second;
    }

}
