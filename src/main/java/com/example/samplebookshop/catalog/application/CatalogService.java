package com.example.samplebookshop.catalog.application;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.catalog.domain.CatalogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {

    private CatalogRepository catalogRepository;

    @Override
    public List<Book> findByTitle(String title){
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().startsWith(title))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().startsWith(author))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findAll(){
        return null;
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author){
        return Optional.empty();
    }

    @Override
    public void addBook(CreateBookCommand command){
        Book book = new Book(command.getTitle(), command.getAuthor(), command.getYear());
        this.catalogRepository.save(book);
    }

    @Override
    public void removeById(Long Id){

    }

    @Override
    public void updateBook(){

    }

}
