package com.metar.decoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MetarDecoderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetarDecoderApplication.class, args);
		System.out.println("Hello World");
	}

}
