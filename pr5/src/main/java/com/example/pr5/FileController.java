package com.example.pr5;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/")
    public String showUploadForm(Model model) {
        model.addAttribute("files", fileService.getFiles());
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            fileService.saveFile(file);
            model.addAttribute("uploadStatus", "File uploaded successfully: " + file.getOriginalFilename());

        } catch (IOException e) {
            model.addAttribute("uploadStatus", "File upload failed: " + e.getMessage());
        }
        model.addAttribute("files", fileService.getFiles());
        return "index";
    }

    @GetMapping("/download/{filename}")
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String filename) {
        File file = fileService.getFile(filename);
        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}