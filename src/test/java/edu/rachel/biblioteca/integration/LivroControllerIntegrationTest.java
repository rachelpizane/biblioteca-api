package edu.rachel.biblioteca.integration;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.model.Livro;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LivroControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private LivroRepository livroRepository;

    @MockitoSpyBean
    private AutorRepository autorRepository;

    public static final String LIVRO_URL = "/livros";

    @AfterEach
    void tearDown() {
        livroRepository.deleteAll();
        autorRepository.deleteAll();
    }

    @Nested
    class CadastrarLivroTests {
        @Test
        void deveCadastrarLivroCorretamente(){
            Autor autor = autorRepository.save(AutorMock.getAutorMock());
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(List.of(autor.getId()));


            ResponseEntity<LivroResponseDTO> response = restTemplate.postForEntity(
                    LIVRO_URL, request, LivroResponseDTO.class);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody().id());
            assertEquals(response.getBody().autores().getFirst().id(), autor.getId());
            verify(livroRepository, times(1)).save(any(Livro.class));
        }

        @Test
        void deveRetornarErroQuandoExistirLivroComIsbn(){
            Autor autor = autorRepository.save(AutorMock.getAutorMock());
            livroRepository.save(LivroMock.getLivroMock(autor));
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(List.of(autor.getId()));

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(LIVRO_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("ISBN"));
            verify(livroRepository, times(1)).save(any(Livro.class));
        }

        @Test
        void deveRetornarErroQuandoAutorNaoEncontrado(){
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(List.of(UUID.randomUUID()));

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(LIVRO_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
            verify(livroRepository, never()).save(any(Livro.class));
        }
    }
}
