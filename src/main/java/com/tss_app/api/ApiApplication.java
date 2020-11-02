package com.tss_app.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.ConnectException;

@SpringBootApplication
@EnableSwagger2
public class ApiApplication {



	public static void main(String[] args) throws ConnectException {SpringApplication.run(ApiApplication.class, args);
	}

}
