package com.example.samplebookshop.catalog.web;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.web.CustomUri;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
public class CatalogController {

    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> findAll(@RequestParam Optional<String> title, @RequestParam Optional<String> author) {
        if (title.isPresent() && author.isPresent()) {
            return catalog.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            return catalog.findByTitle(title.get());
        } else if (author.isPresent()) {
            return catalog.findByAuthor(author.get());
        } else {
            return catalog.findAll();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> findOneById(@PathVariable Long id) {
        return catalog
                .findOneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addBook(@Valid @RequestBody RestBookCommand command) {
        Book book = this.catalog.addBook(command.toCreateCommand());
        URI uri = createBookUri(book);
        return ResponseEntity.created(uri).build();
    }

    private URI createBookUri(Book book) {
        return new CustomUri("/" + book.getId().toString()).toUri();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        catalog.removeById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody RestBookCommand command) {
        UpdateBookResponse response = catalog.updateBook(command.toUpdateBookCommand(id));
        if (!response.isSuccess()) {
            String errorMessage = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    @PutMapping(value = "/{id}/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Got file: " + file.getOriginalFilename());
        catalog.updateBookCover(new UpdateBookCoverCommand(
                id,
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()
        ));
    }

    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable Long id){
        catalog.removeBookCover(id);
    }

    @Data
    private static class RestBookCommand {
        @NotBlank(message = "Please enter a title")
        private String title;

        @NotBlank(message = "Please enter an author")
        private String author;

        @NotNull(message = "Please enter the year")
        private Integer year;

        @NotNull(message = "Please enter the price")
        @DecimalMin(value = "0.00", message = "Price cannot be negative")
        private BigDecimal price;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(title, author, year, price);
        }

        UpdateBookCommand toUpdateBookCommand(Long id) {
            return new UpdateBookCommand(id, title, author, year, price);
        }
    }

}
