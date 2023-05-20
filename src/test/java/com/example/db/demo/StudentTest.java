package com.example.db.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.db.demo.service.StudentService;


public class StudentTest extends ApplicationTests {

    @Autowired
    private StudentService service;

    @Test
    public void testFindTwice() {
        //service.init();
        Assertions.assertTrue(service.findTwice(1L));
    }
}
