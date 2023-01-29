package com.example.db.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.example.db.demo.entity.Student;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student> {
}
