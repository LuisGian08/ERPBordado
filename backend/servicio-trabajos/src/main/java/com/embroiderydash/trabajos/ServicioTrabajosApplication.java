package com.embroiderydash.trabajos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ServicioTrabajosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioTrabajosApplication.class, args);
    }
}
