package com.example.vkrfull.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileData {
    private String contentType;
    private String filename;
    private Long size;
}
