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

    @GetMapping("/request-response/{id}")
    public Mono<MyData> sendRequestResponse(@PathVariable Long id) {
        log.info("Sending request / response");
        return rSocketRequester.route("responder-request-response.{id}", id)
                .retrieveMono(MyData.class);
    }

    @GetMapping("/request-stream")
    public Flux<MyData> sendChannelStream() {
        log.info("Sending request stream");
        return rSocketRequester.route("responder-request-stream")
                .retrieveFlux(MyData.class);

    }

    @PostMapping(value = "/fire-forget")
    public Mono<Void> sendFireForget(@RequestBody MyData data) {
        log.info("Sending fire and forget");
        return rSocketRequester.route("responder-fire-forget")
                .data(data)
                .send();
    }

    @PostMapping(value = "/channel")
    public Flux<MyData> sendChannel(@RequestBody List<MyData> data) {
        log.info("Sending channel");

        return rSocketRequester.route("responder-channel")
                .data(Flux.fromIterable(data))
                .retrieveFlux(MyData.class);
    }
}
