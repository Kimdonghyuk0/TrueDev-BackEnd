package com.kdh.truedev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)


@EnableJpaAuditing
@SpringBootApplication
public class TrueDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrueDevApplication.class, args);
	}

}
