package com.robert.leave_ms_bn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(scanBasePackages = "com.robert.leave_ms_bn")
public class LeaveMsBnApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaveMsBnApplication.class, args);
    }
}
