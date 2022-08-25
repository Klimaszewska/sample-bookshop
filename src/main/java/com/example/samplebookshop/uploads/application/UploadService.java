package com.example.samplebookshop.uploads.application;

import com.example.samplebookshop.uploads.application.port.UploadUseCase;
import com.example.samplebookshop.uploads.domain.Upload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadService implements UploadUseCase {
    private final Map<Long, Upload> storage = new ConcurrentHashMap<>();

    @Override
    public Upload save(SaveUploadCommand command) {
        Long id = Math.abs(new Random().nextLong());
        Upload upload = new Upload(
                id,
                command.getFile(),
                command.getContentType(),
                command.getFilename(),
                LocalDateTime.now()
        );
        storage.put(upload.getId(), upload);
        System.out.println("Upload saved: " + upload.getFilename() + " With id: " + id);
        return upload;
    }

}
