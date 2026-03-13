package edu.rachel.biblioteca.integration;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.model.Locatario;
import edu.rachel.biblioteca.repository.LocatarioRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LocatarioControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private LocatarioRepository repository;

    public static final String LOCATARIO_URL = "/locatarios";

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Nested
    class CadastrarLocatarioTests {
        @Test
        void deveCadastrarLocatarioCorretamente(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            ResponseEntity<LocatarioResponseDTO> response = restTemplate
                    .postForEntity(LOCATARIO_URL, request, LocatarioResponseDTO.class);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody().id());
            verify(repository, times(1)).save(any(Locatario.class));
        }

        @Test
        void deveRetornarErroQuandoExistirLocatarioComCPF(){
            repository.save(LocatarioMock.getLocatarioMock());

            LocatarioRequestDTO request = LocatarioMock
                    .getRequestComEmail("outro.email@email.com");

            ResponseEntity<ErrorResponseDTO> response = restTemplate
                    .postForEntity(LOCATARIO_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("CPF"));
            verify(repository, times(1)).save(any(Locatario.class));
        }

        @Test
        void deveRetornarErroQuandoExistirLocatarioComEmail(){
            repository.save(LocatarioMock.getLocatarioMock());

            LocatarioRequestDTO request = LocatarioMock
                    .getRequestComCpf("12345678901");

            ResponseEntity<ErrorResponseDTO> response = restTemplate
                    .postForEntity(LOCATARIO_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("e-mail"));
            verify(repository, times(1)).save(any(Locatario.class));
        }
    }

    @Nested
    class BuscarLocatarioTests {
        @Test
        void deveBuscarLocatarioComSucesso(){
            Locatario locatario = repository.save(LocatarioMock.getLocatarioMock());

            ResponseEntity<LocatarioResponseDTO> response = restTemplate.getForEntity(
                    LOCATARIO_URL + "/{id}",
                    LocatarioResponseDTO.class,
                    locatario.getId()
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody().id(), locatario.getId());
        }

        @Test
        void deveRetornarErroQuandoLocatarioNaoExistir(){
            ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
                    LOCATARIO_URL + "/{id}",
                    ErrorResponseDTO.class,
                    UUID.randomUUID()
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
        }
    }
}
