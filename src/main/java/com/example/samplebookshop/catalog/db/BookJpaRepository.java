package com.example.samplebookshop.catalog.db;

import com.example.samplebookshop.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
}
