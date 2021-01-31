package com.example.springreactiveweb.web;

import com.example.springreactiveweb.domain.Customer;
import com.example.springreactiveweb.domain.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

// https://howtodoinjava.com/spring-webflux/webfluxtest-with-webtestclient/
// webflux junit5 로 테스트 하는 법

// 통합테스트
@SpringBootTest
@AutoConfigureWebTestClient
class CustomerControllerTest {

//    @MockBean
//    CustomerRepository customerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    public void 전체찾기_테스트() {
        System.out.println("==================================");
        Flux<Customer> fCustomer = customerRepository.findAll();
        fCustomer.subscribe((t) -> {
            System.out.println("데이터");
            System.out.println(t);
        });
    }
}