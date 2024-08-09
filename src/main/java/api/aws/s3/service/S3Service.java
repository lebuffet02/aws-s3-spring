package api.aws.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class S3Service {

    @Value("${aws.bucketName}")
    private String bucket;

    @Autowired
    private AmazonS3 s3Client;

    private static final String REGEX_EXTENSAO = ".*\\.(jpeg|jpg|png|gif|txt|pdf)$";

    public ResponseEntity<?> uploadService(List<MultipartFile> files) {
        try {
            files.forEach(f -> {
                String keyName = StringUtils.cleanPath(f.getOriginalFilename());
                try {
                    s3Client.putObject(this.bucket, keyName, f.getInputStream(), getObjectMetadata(f));
                    log.info("Sucesso ao realizar upload para o bucket: {} , da imagem: {}, {}", this.bucket, f.getOriginalFilename(), keyName);
                } catch (Exception e) {
                    log.info("Falha ao realizar upload para o bucket: {} , da imagem: {}", this.bucket, f.getOriginalFilename());
                    throw e instanceof AmazonServiceException ? new AmazonServiceException("Detail Message: " + e.getMessage()) : new RuntimeException(e.getMessage());
                }
            });
            return ResponseEntity.ok().body("Sucesso ao realizar upload...");
        } catch (Exception e) {
            log.info("Falha ao realizar upload para o bucket: {}  ", this.bucket);
            return e instanceof AmazonServiceException ? ResponseEntity.badRequest().body("Detail Message: " + e.getMessage()) : ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    public ResponseEntity<?> deleteService(String nameFile) {
        try {
            if (!Pattern.compile(REGEX_EXTENSAO).matcher(nameFile).find()) {
                return ResponseEntity.badRequest().body("Falha ao deletar arquivo devido a extensão...");
            }
            s3Client.deleteObject(this.bucket, nameFile);
            log.info("Sucesso ao deletar arquivo do bucket: {}  ", this.bucket);
            return ResponseEntity.ok().body("Sucesso ao deletar arquivo...");
        } catch (Exception e) {
            log.info("Falha ao deletar arquivo do bucket: {} ", this.bucket);
            return ResponseEntity.badRequest().body("Falha ao deletar arquivo ".concat(nameFile));
        }
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }

    public ByteArrayOutputStream downloadService(String nameFile) {
        try {
            return getOutPut(s3Client.getObject(this.bucket, nameFile).getObjectContent(), nameFile);
        } catch (Exception e) {
            log.info("Falha ao baixar arquivo do bucket: {} , {} ", this.bucket, e.getMessage());
            return null;
        }
    }

    private ByteArrayOutputStream getOutPut(InputStream input, String nameFile) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = input.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            log.info("Sucesso ao baixar arquivo do bucket({}): {}", this.bucket, nameFile);
            return out;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}