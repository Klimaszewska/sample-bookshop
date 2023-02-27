package com.example.samplebookshop.catalog.application;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.db.AuthorJpaRepository;
import com.example.samplebookshop.catalog.db.BookJpaRepository;
import com.example.samplebookshop.catalog.domain.Author;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.uploads.application.port.UploadUseCase;
import com.example.samplebookshop.uploads.application.port.UploadUseCase.SaveUploadCommand;
import com.example.samplebookshop.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {

    private final BookJpaRepository catalogRepository;
    private final AuthorJpaRepository authorRepository;
    private final UploadUseCase uploadUseCase;

    @Override
    public List<Book> findAll() {
        return catalogRepository.findAllBooksWithAuthors();
    }

    @Override
    public List<Book> findByTitle(String title) {
        return catalogRepository.findByTitleStartsWithIgnoreCase(title);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return catalogRepository.findByAuthor(author);
    }

    @Override
    public Optional<Book> findOneById(Long id) {
        return catalogRepository.findById(id);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return catalogRepository.findDistinctFirstByTitleStartsWithIgnoreCase(title);
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return catalogRepository.findByTitleAndAuthor(title, author);
    }

    @Override
    @Transactional
    public Book addBook(CreateBookCommand command) {
        Book book = toBook(command);
        return this.catalogRepository.save(book);
    }

    @Override
    public void removeById(Long id) {
        this.catalogRepository.deleteById(id);

    }

    @Override
    @Transactional
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return catalogRepository.findById(command.getId())
                .map(book -> {
                    updateFields(command, book);
                    return UpdateBookResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateBookResponse(false, Collections.singletonList("Book not found. Id: " + command.getId())));

    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        catalogRepository.findById(command.getId())
                .ifPresent(book -> {
                    Upload savedUpload = uploadUseCase.save(new SaveUploadCommand(command.getFilename(), command.getFile(), command.getContentType()));
                    book.setCoverId(savedUpload.getId());
                    catalogRepository.save(book);
                });
    }

    @Override
    public void removeBookCover(Long id) {
        catalogRepository.findById(id)
                .ifPresent(book -> {
                    if (book.getCoverId() != null) {
                        uploadUseCase.removeById(book.getCoverId());
                        book.setCoverId(null);
                        catalogRepository.save(book);
                    }
                });
    }

    private Book toBook(CreateBookCommand command) {
        Book book = new Book(command.getTitle(), command.getYear(), command.getPrice(), command.getAvailableBooks());
        Set<Author> authors = getAuthorsByIds(command.getAuthorIds());
        setAuthors(book, authors);
        return book;
    }

    private Set<Author> getAuthorsByIds(Set<Long> authorIds) {
        return authorIds.stream()
                .map(authorId -> authorRepository
                        .findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("Unable to find author with id: " + authorId))
                )
                .collect(Collectors.toSet());
    }

    private Book updateFields(UpdateBookCommand command, Book book) {
        if (command.getTitle() != null) {
            book.setTitle(command.getTitle());
        }
        if (command.getAuthorIds() != null && command.getAuthorIds().size() > 0) {
            Set<Author> authors = getAuthorsByIds(command.getAuthorIds());
            setAuthors(book, authors);
        }
        if (command.getYear() != null) {
            book.setYear(command.getYear());
        }
        if (command.getPrice() != null) {
            book.setPrice(command.getPrice());
        }
        return book;
    }

    private void setAuthors(Book book, Set<Author> authors) {
        book.removeAllAuthors();
        authors.forEach(book::addAuthor);
    }
}
