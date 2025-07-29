package com.iam.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {"com.iam.user", "com.iam.common"})
@EnableJpaRepositories(basePackages = "com.iam.user.repository")
@EntityScan(basePackages = {"com.iam.common.model"})
@EnableTransactionManagement
public class UserServiceApplication {
    public static void main (String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
