package com.example.samplebookshop.uploads.application.port;

import com.example.samplebookshop.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import lombok.Value;

public interface UploadUseCase {

    Upload save(SaveUploadCommand command);

    @Value
    @AllArgsConstructor
    class SaveUploadCommand {
        String filename;
        byte[] file;
        String contentType;
    }
}
