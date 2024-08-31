package com.example.pr7;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    @Autowired
    WebTestClient webClient;

    @Test
    void testGetBooks() {
        webClient.get().uri("/api/books")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Book.class);
    }

    @Test
    void testGetBook() {
        webClient.get().uri("/api/books/1")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Book.class);
    }

    @Test
    void testAddNewBook() {
        Book newBook = new Book();
        newBook.setAuthor("author1");
        newBook.setTitle("title1");

        webClient.post().uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newBook), Book.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.author").isEqualTo(newBook.getAuthor())
                .jsonPath("$.title").isEqualTo(newBook.getTitle());
    }

    @Test
    void testUpdateBook() {
        Book newBook = new Book();
        newBook.setId(1L);
        newBook.setAuthor("authorUpdate");
        newBook.setTitle("titleUpdate");

        webClient.put().uri("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newBook), Book.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(newBook.getId())
                .jsonPath("$.author").isEqualTo(newBook.getAuthor())
                .jsonPath("$.title").isEqualTo(newBook.getTitle());
    }

    @Test
    void testDeleteBook() {

        webClient.delete().uri("/api/books/1")
                .exchange()
                .expectStatus().isNoContent();

        webClient.get().uri("/api/books/1")
                .exchange()
                .expectStatus().isNotFound();

    }
}

