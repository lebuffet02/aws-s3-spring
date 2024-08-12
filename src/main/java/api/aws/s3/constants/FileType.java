package api.aws.s3.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;
import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum FileType {

    JPG("jpg", MediaType.IMAGE_JPEG),
    JPEG("jpeg", MediaType.IMAGE_JPEG),
    TXT("txt", MediaType.TEXT_PLAIN),
    PNG("png", MediaType.IMAGE_PNG),
    PDF("pdf", MediaType.APPLICATION_PDF);

    private final String extension;
    private final MediaType mediaType;

    public static MediaType fileType(String fileName) {
        return Arrays
                .stream(values())
                .filter(e -> e.getExtension()
                        .equalsIgnoreCase(
                                Optional.ofNullable(fileName)
                                        .filter(f -> f.contains("."))
                                        .map(f -> f.substring(
                                                fileName.lastIndexOf('.') + 1))
                                        .orElse("")))
                .findFirst()
                .map(FileType::getMediaType)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }
}