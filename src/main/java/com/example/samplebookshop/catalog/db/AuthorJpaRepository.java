package com.example.samplebookshop.catalog.db;

import com.example.samplebookshop.catalog.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
}
