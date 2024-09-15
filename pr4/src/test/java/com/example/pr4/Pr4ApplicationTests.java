package com.example.pr4;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Pr4ApplicationTests {


    @Autowired
    private MyRepository myRepository;
    private static RSocketRequester rSocketRequester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder) {
        rSocketRequester = builder.tcp("localhost", 7000);
    }

    @Test
    void testAddData() {
        Mono<MyData> result = rSocketRequester
                .route("addData")
                .data(new MyData( "qwe", 12, 12.0f))
                .retrieveMono(MyData.class);
        StepVerifier
                .create(result)
                .consumeNextWith(notification -> {
                    assertThat(notification.getTitle()).isEqualTo("qwe");
                    assertThat(notification.getAmount()).isEqualTo(12);
                    assertThat(notification.getPrice()).isEqualTo(12.0f);
                })
                .verifyComplete();

        MyData savedData = result.block();
        assertNotNull(savedData);
        assertNotNull(savedData.getId());
        assertTrue(savedData.getId() > 0);
    }

    @Test
    void testGetData() {
        MyData myData = new MyData("qwe", 12, 12.0f);

        MyData savedMyData = myRepository.save(myData);

        Mono<MyData> result = rSocketRequester
                .route("responder-request-response.{id}", savedMyData.getId())
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

    @Test
    void testGetAllData() {
        Flux<MyData> result = rSocketRequester
                .route("responder-request-stream")
                .retrieveFlux(MyData.class);

        assertNotNull(result.blockFirst());
    }

    @Test
    public void testDeleteData() {
        MyData myData = new MyData("qwe", 12, 12.0f);
        MyData savedMyData = myRepository.save(myData);
        Mono<Void> result = rSocketRequester.route("deleteData")
                .data(savedMyData.getId())
                .send();
        result.block();
        MyData deletedMyData = myRepository.findMyDataById(savedMyData.getId());
        assertNotSame(deletedMyData, savedMyData);
    }
}
