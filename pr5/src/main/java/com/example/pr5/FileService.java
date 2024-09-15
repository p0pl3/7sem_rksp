package com.example.pr5;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
//    private final String fileStorageLocation = System.getProperty("user.dir") + "/src/main/resources/static/images/";
    private final String fileStorageLocation = "/upload-files/";

    public FileService() {
        // Создаем директорию для хранения загруженных файлов
        new File(fileStorageLocation).mkdirs();
    }

    public void saveFile(MultipartFile file) throws IOException {
        Path path = Paths.get(fileStorageLocation + file.getOriginalFilename());
        Files.copy(file.getInputStream(), path);
    }

    public File getFile(String filename) {
        return new File(fileStorageLocation + filename);
    }

    public List<String> getFiles() {
        List<String> files = new ArrayList<>();
        File folder = new File(fileStorageLocation);
        for (final File fileEntry : folder.listFiles()) {
            files.add(fileEntry.getName());
        }
        return files;
    }
}

