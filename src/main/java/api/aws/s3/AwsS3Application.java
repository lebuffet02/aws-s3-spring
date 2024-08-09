package api.aws.s3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class AwsS3Application {

	public static void main(String[] args) {
		SpringApplication.run(AwsS3Application.class, args);
	}
}