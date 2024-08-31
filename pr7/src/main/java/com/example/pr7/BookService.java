package com.example.pr7;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Mono<Book> createBook(Book book) {
        return bookRepository.save(book);
    }

    public Mono<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Flux<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    public Mono<Book> updateBook(Long id, Book book) {
        return bookRepository.findById(id)
                .flatMap(existingBook -> {
                    existingBook.setIsbn(book.getIsbn());
                    existingBook.setAuthor(book.getAuthor());
                    existingBook.setYear(book.getYear());
                    existingBook.setTitle(book.getTitle());
                    return bookRepository.save(existingBook);
                });

    }

    public Mono<Void> deleteBook(Book book) {
        return bookRepository.deleteById(book.getId());
    }
}
