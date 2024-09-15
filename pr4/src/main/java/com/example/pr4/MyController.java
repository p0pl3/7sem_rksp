package com.example.pr4;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Log4j2
@Controller
@RequiredArgsConstructor
public class MyController {

    private final MyRepository myRepository;

    @MessageMapping("responder-request-response.{id}")
    public Mono<MyData> findById(@DestinationVariable Long id) {
        log.info("Request / Response");
        return Mono.justOrEmpty(myRepository.findMyDataById(id)).doOnNext(log::info);
    }

    @MessageMapping("responder-request-stream")
    public Flux<MyData> findAll() {
        return Flux.fromIterable(myRepository.findAll());
    }

    @MessageMapping("addData")
    public Mono<MyData> create(MyData marketData) {
        log.info("Fire and Forget");
        return Mono.justOrEmpty(myRepository.save(marketData));
    }

    @MessageMapping("responder-channel")
    public Flux<MyData> update(Flux<MyData> datas) {
        log.info("Channel");
        return datas.doOnNext(myRepository::save);
    }

    @MessageMapping("deleteData.{id}")
    public Mono<Void> deleteCat(@DestinationVariable Long id) {
        MyData myData = myRepository.findMyDataById(id);
        myRepository.delete(myData);
        return Mono.empty();
    }
}