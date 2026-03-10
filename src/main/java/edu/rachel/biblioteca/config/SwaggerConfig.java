package edu.rachel.biblioteca.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Biblioteca API",
                version = "1.0",
                summary = "API para gerenciamento de uma biblioteca escolar responsável pelo controle de livros, autores, locatários e aluguéis."
        )
)
public class SwaggerConfig {
}
