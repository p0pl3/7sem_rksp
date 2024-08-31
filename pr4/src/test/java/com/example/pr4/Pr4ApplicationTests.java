package com.example.pr4;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class Pr4ApplicationTests {
    private static RSocketRequester rSocketRequester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder) {
        rSocketRequester = builder.tcp("localhost", 7000);
    }

    @Test
    void testFireAndForget() {
        // Send a fire-and-forget message
        Mono<Void> result = rSocketRequester
                .route("responder-fire-forget")
                .data(new MyData(1L, "qwe", 12, 12.0f))
                .retrieveMono(Void.class);

        // Assert that the result is a completed Mono.
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void testRequestResponse() {
        Mono<MyData> result = rSocketRequester
                .route("responder-request-response.{id}", 1L)
                .retrieveMono(MyData.class);
        StepVerifier
                .create(result)
                .consumeNextWith(notification -> {
                    assertThat(notification.getTitle()).isEqualTo("qwe");
                    assertThat(notification.getAmount()).isEqualTo(12);
                    assertThat(notification.getPrice()).isEqualTo(12.0f);
                })
                .verifyComplete();
    }


}
