package com.example.springreactiveweb.web;

import com.example.springreactiveweb.domain.Customer;
import com.example.springreactiveweb.domain.CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final Sinks.Many<Customer> sink;

    // A 요청 -> Flux -> Stream
    // B 요청 -> Flux -> Stream
    // -> Flux.merge -> sink

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @GetMapping("/flux")
    public Flux<Integer> flux() {
        // Flux.just : 데이터를 순차적으로 가져와서 onNext 로 던져준다.
        return Flux.just(1, 2, 3, 4, 5).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> fluxstream() {
        // 한건 onNext 할때마다 버퍼를 flush 한다. 즉, 한건씩 간다.
        return Flux.just(1, 2, 3, 4, 5).delayElements(Duration.ofSeconds(1)).log();
    }

    // 데이터가 소진되면 응답이 종료된다.
    @GetMapping(value = "/customer", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Customer> findAll() {
        // onComplete() 되는 순간 응답이 됨
        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }

    // 데이터가 소진되면 응답이 종료된다.
    @GetMapping("/customer/{id}")
    public Mono<Customer> findById(@PathVariable Long id) {
        // Mono 는 한건밖에 없을 때 !!
        return customerRepository.findById(id).log();
    }

    // 프론트단에서 sse 를 구독하는 방법
    // https://developer.mozilla.org/ko/docs/Web/API/EventSource

    /**
     * var evtSource = new EventSource('http://localhost/customer/sse');
     * var eventList = document.querySelector('ul');
     *
     * evtSource.onmessage = function(e) {
     *   var newElement = document.createElement("li");
     *
     *   newElement.textContent = "message: " + e.data;
     *   eventList.appendChild(newElement);
     * }
     */

    @GetMapping(value = "/customer/sse") // produces = MediaType.TEXT_EVENT_STREAM_VALUE 생략 가능!
    public Flux<ServerSentEvent<Customer>> findAllSSE() {
//        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
        // 응답이 끝나도 안멈추게하려면 sink 를 활용한다.
        return sink.asFlux().map(c -> ServerSentEvent.builder(c).build()).doOnCancel(() -> {
            sink.asFlux().blockLast(); // 응답을 중단해도 다음 연결시에 그대로 연결해서 쓸 수 있다.
        });
    }

    @PostMapping("/customer")
    public Mono<Customer> save() {
        // 저장이 되고난 후 sink 에 push 해야함
        return customerRepository.save(new Customer("gildong", "Hong'")).doOnNext(c -> {
            sink.tryEmitNext(c);
        });
    }
}