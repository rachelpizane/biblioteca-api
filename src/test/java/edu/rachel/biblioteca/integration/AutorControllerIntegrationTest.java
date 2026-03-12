package edu.rachel.biblioteca.integration;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.dto.ErrorResponseDTO;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.repository.AutorRepository;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AutorControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private AutorRepository repository;

    public static final String AUTOR_URL = "/autores";

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Nested
    class CadastrarAutorTests {
        @Test
        void deveCadastrarAutorCorretamente(){
            AutorRequestDTO request = AutorMock.getAutorRequestDTOMock();

            ResponseEntity<AutorResponseDTO> response = restTemplate.postForEntity(AUTOR_URL, request, AutorResponseDTO.class);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody().id());
            verify(repository, times(1)).save(any(Autor.class));
        }

        @Test
        void deveRetornarErroQuandoExistirAutorCPF(){
            repository.save(AutorMock.getAutorMock());

            AutorRequestDTO request = AutorMock.getAutorRequestDTOMock();

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(AUTOR_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("CPF"));
            verify(repository, times(1)).save(any(Autor.class));
        }
    }

    @Nested
    class BuscarAutorTests{
        @Test
        void deveBuscarAutorComSucesso(){
            Autor autor = repository.save(AutorMock.getAutorMock());

            ResponseEntity<AutorResponseDTO> response = restTemplate.getForEntity(
                    AUTOR_URL + "/{id}",
                    AutorResponseDTO.class,
                    autor.getId()
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody().id(), autor.getId());
        }

        @Test
        void deveRetornarErroQuandoAutorNaoExistir(){
            ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
                    AUTOR_URL + "/{id}",
                    ErrorResponseDTO.class,
                    UUID.randomUUID()
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
        }
    }
}
