package com.example.samplebookshop.catalog.db;

import com.example.samplebookshop.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
    //query example for a ManyToMany relation
    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(@Param("firstName") String firstName, @Param("lastName") String lastName);

    //query example for eager loading
    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.authors")
    List<Book> findAllBooksWithAuthors();

    //simple query example
    List<Book> findByTitleStartsWithIgnoreCase(@Param("title") String title);

    Optional<Book> findDistinctFirstByTitleStartsWithIgnoreCase(@Param("title") String title);

    //sql query example
    @Query(" SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(a.firstName) LIKE lower(concat('%', :name, '%')) " +
            " OR lower(a.lastName) LIKE lower(concat('%', :name, '%')) "
    )
    List<Book> findByAuthor(@Param("name") String name);

    @Query(" SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(b.title) LIKE lower(concat('%', :title, '%')) " +
            " AND" +
            " lower(concat(a.firstName, ' ', a.lastName)) LIKE lower(concat('%', :author, '%'))"
    )
    List<Book> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);
}
