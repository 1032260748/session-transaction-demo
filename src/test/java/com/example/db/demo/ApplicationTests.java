package com.example.db.demo;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { Application.class }, webEnvironment = RANDOM_PORT, properties = {
        "spring.profiles.active=local" })
public class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
