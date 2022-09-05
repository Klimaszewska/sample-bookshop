package com.example.samplebookshop.uploads.db;

import com.example.samplebookshop.uploads.domain.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {
}
