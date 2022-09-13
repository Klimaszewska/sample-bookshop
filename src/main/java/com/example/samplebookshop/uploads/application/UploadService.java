package com.example.samplebookshop.uploads.application;

import com.example.samplebookshop.uploads.application.port.UploadUseCase;
import com.example.samplebookshop.uploads.db.UploadJpaRepository;
import com.example.samplebookshop.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UploadService implements UploadUseCase {
    private final UploadJpaRepository repository;


    @Override
    public Upload save(SaveUploadCommand command) {
        Upload upload = new Upload(
                command.getFilename(),
                command.getContentType(),
                command.getFile()
        );
        repository.save(upload);
        log.info("Upload saved: " + upload.getFilename() + " With id: " + upload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        Upload upload = repository.getById(id);
        return Optional.ofNullable(upload);
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }

}
