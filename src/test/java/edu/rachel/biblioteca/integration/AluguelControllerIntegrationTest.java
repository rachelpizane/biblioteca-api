package edu.rachel.biblioteca.integration;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;
import edu.rachel.biblioteca.dto.ErrorResponseDTO;
import edu.rachel.biblioteca.mock.AluguelMock;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.model.Aluguel;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.model.Livro;
import edu.rachel.biblioteca.model.Locatario;
import edu.rachel.biblioteca.repository.AluguelRepository;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AluguelControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private AluguelRepository aluguelRepository;

    @MockitoSpyBean
    private LocatarioRepository locatarioRepository;

    @MockitoSpyBean
    private AutorRepository autorRepository;

    @MockitoSpyBean
    private LivroRepository livroRepository;

    public static final String ALUGUEL_URL = "/alugueis";

    @AfterEach
    void tearDown() {
        aluguelRepository.deleteAll();
        locatarioRepository.deleteAll();
        livroRepository.deleteAll();
        autorRepository.deleteAll();
    }

    @Nested
    class CadastrarAluguelTests {
        @Test
        void deveCadastrarAluguelCorretamente(){
            Autor autor = autorRepository.save(AutorMock.getAutorMock());
            Livro livro = livroRepository.save(LivroMock.getLivroMock(autor));
            Locatario locatario = locatarioRepository.save(LocatarioMock.getLocatarioMock());

            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(locatario.getId(), List.of(livro.getId()));

            ResponseEntity<AluguelResponseDTO> response = restTemplate.postForEntity(
                    ALUGUEL_URL, request, AluguelResponseDTO.class);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody().id());
            assertEquals(response.getBody().locatario().id(), locatario.getId());
            assertEquals(response.getBody().livros().getFirst().id(), livro.getId());
            verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        }

        @Test
        void deveRetornarConflitoQuandoLivroEstiverAlugado(){
            Autor autor = autorRepository.save(AutorMock.getAutorMock());
            Livro livro = livroRepository.save(LivroMock.getLivroMock(autor));
            Locatario locatario = locatarioRepository.save(LocatarioMock.getLocatarioMock());
            aluguelRepository.save(AluguelMock.getAluguelMock(locatario.getId(), List.of(livro.getId())));

            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(locatario.getId(), List.of(livro.getId()));

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(ALUGUEL_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("aluguel em andamento"));
            verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        }

        @Test
        void deveRetornarErroQuandoLocatarioNaoForEncontrado(){
            Autor autor = autorRepository.save(AutorMock.getAutorMock());
            Livro livro = livroRepository.save(LivroMock.getLivroMock(autor));

            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(UUID.randomUUID(), List.of(livro.getId()));

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(ALUGUEL_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("Locatário não encontrado"));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        void deveRetornarErroQuandoLivroNaoForEncontrado(){
            Locatario locatario = locatarioRepository.save(LocatarioMock.getLocatarioMock());

            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(locatario.getId(), List.of(UUID.randomUUID()));

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(ALUGUEL_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("Livros não encontrados"));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }
    }
}
