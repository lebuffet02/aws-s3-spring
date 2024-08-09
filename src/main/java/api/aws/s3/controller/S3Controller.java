package api.aws.s3.controller;

import api.aws.s3.constants.FileType;
import api.aws.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("s3")
public class S3Controller {

    @Autowired
    S3Service service;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadController(@RequestParam("file") List<MultipartFile> files) {
        return service.uploadService(files);
    }

    @GetMapping("/{file}")
    public ResponseEntity<?> downloadController(@PathVariable("file") String nameFile) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nameFile + "\"")
                .contentType(FileType.fileType(nameFile)).body(service.downloadService(nameFile) != null ? service.downloadService(nameFile).toByteArray()
                        : ResponseEntity.badRequest().body("Falha ao baixar arquivo..."));
    }

    @DeleteMapping("/{file}")
    public ResponseEntity<?> deleteController(@PathVariable("file") String nameFile) {
        return service.deleteService(nameFile);
    }
}