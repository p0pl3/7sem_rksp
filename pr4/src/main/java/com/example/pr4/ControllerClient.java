package com.example.pr4;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Flow;

@Log4j2
@RestController
@RequiredArgsConstructor
public class ControllerClient {
    private final RSocketRequester rSocketRequester;

    @GetMapping("/get_one/{id}")
    public Mono<MyData> sendRequestResponse(@PathVariable Long id) {
        log.info("Sending request / response");
        return rSocketRequester.route("responder-request-response.{id}", id)
                .retrieveMono(MyData.class);
    }

    @GetMapping("/get_all")
    public Flux<MyData> sendChannelStream() {
        log.info("Sending request stream");
        return rSocketRequester.route("responder-request-stream")
                .retrieveFlux(MyData.class);
    }

    @PostMapping(value = "/add_data")
    public Mono<MyData> sendAddData(@RequestBody MyData data) {
        log.info("Sending fire and forget");
        return rSocketRequester.route("addData")
                .data(data)
                .retrieveMono(MyData.class);
    }

    @PostMapping(value = "/update_many")
    public Flux<MyData> sendChannel(@RequestBody List<MyData> data) {
        log.info("Sending channel");

        return rSocketRequester.route("responder-channel")
                .data(Flux.fromIterable(data))
                .retrieveFlux(MyData.class);
    }

    @DeleteMapping(value = "/delete/{id}")
    public Mono<Void> deleteData(@PathVariable Long id){
        return rSocketRequester.route("deleteData.{id}", id)
                .send();
    }
}
