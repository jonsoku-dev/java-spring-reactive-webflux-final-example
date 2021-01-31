package com.example.springreactiveweb.domain;

import com.example.springreactiveweb.DBInit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(DBInit.class)
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void 한건찾기_테스트() {
        customerRepository.findById(1L).subscribe((c) -> {
            System.out.println(c);
        });

        StepVerifier
                .create(customerRepository.findById(2L))
                .expectNextMatches((c) -> {
                    return c.getFirstName().equals("Chloe");
                })
                .expectComplete()
                .verify();

    }
}

/*
 * webflux test 참고자료
 * http://jalbertomr.blogspot.com/2019/12/road-reactive-spring-junit-test-webflux.html
 * https://www.baeldung.com/reactive-streams-step-verifier-test-publisher
 */