package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({nlp.service.controller.ServiceController.class,
		nlp.service.config.ApplicationConfiguration.class})

public class ServiceApplication {

	public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
	}

}

