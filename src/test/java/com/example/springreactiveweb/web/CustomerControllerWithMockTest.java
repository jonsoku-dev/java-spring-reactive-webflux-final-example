package com.example.springreactiveweb.web;

import com.example.springreactiveweb.domain.Customer;
import com.example.springreactiveweb.domain.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

// https://howtodoinjava.com/spring-webflux/webfluxtest-with-webtestclient/
// webflux junit5 로 테스트 하는 법

// 컨트롤러만 테스트
@WebFluxTest
class CustomerControllerWithMockTest {

    @MockBean
    CustomerRepository customerRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void 한건찾기_테스트() {
        // given
        Mono<Customer> givenData = Mono.just(new Customer("Jack", "Bauer"));

        // stub -> 행동 지시
        // repository 자체가 mock 이니까 ... return 값을 임의로 지정해줘야한다.
        when(customerRepository.findById(1L)).thenReturn(givenData);

        webTestClient.get().uri("/customer/{id}", 1L)
                .exchange()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Jack")
                .jsonPath("$.lastName").isEqualTo("Bauer");

    }
}