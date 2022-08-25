package com.example.samplebookshop.uploads.domain;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Upload {
    Long id;
    byte[] file;
    String contentType;
    String filename;
    LocalDateTime createdAt;
}
