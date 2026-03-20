package edu.rachel.biblioteca.integration;

import edu.rachel.biblioteca.dto.*;
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
import edu.rachel.biblioteca.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LocatarioControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private LocatarioRepository locatarioRepository;

    @MockitoSpyBean
    private AluguelRepository aluguelRepository;

    @MockitoSpyBean
    private AutorRepository autorRepository;

    @MockitoSpyBean
    private LivroRepository livroRepository;

    @AfterEach
    void tearDown() {
        aluguelRepository.deleteAll();
        locatarioRepository.deleteAll();
        livroRepository.deleteAll();
        autorRepository.deleteAll();
    }

    @Nested
    class CadastrarLocatarioTests {
        @Test
        void deveCadastrarLocatarioCorretamente(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            ResponseEntity<LocatarioResponseDTO> response = restTemplate
                    .postForEntity(Constants.LOCATARIO_URL, request, LocatarioResponseDTO.class);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody().id());
            verify(locatarioRepository, times(1)).save(any(Locatario.class));
        }

        @Test
        void deveRetornarErroQuandoExistirLocatarioComCPF(){
            locatarioRepository.save(LocatarioMock.getLocatarioMock());

            LocatarioRequestDTO request = LocatarioMock
                    .getRequestComEmail("outro.email@email.com");

            ResponseEntity<ErrorResponseDTO> response = restTemplate
                    .postForEntity(Constants.LOCATARIO_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("CPF"));
            verify(locatarioRepository, times(1)).save(any(Locatario.class));
        }

        @Test
        void deveRetornarErroQuandoExistirLocatarioComEmail(){
            locatarioRepository.save(LocatarioMock.getLocatarioMock());

            LocatarioRequestDTO request = LocatarioMock
                    .getRequestComCpf("12345678901");

            ResponseEntity<ErrorResponseDTO> response = restTemplate
                    .postForEntity(Constants.LOCATARIO_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("e-mail"));
            verify(locatarioRepository, times(1)).save(any(Locatario.class));
        }
    }

    @Nested
    class BuscarLocatarioTests {
        @Test
        void deveBuscarLocatarioComSucesso(){
            Locatario locatario = locatarioRepository.save(LocatarioMock.getLocatarioMock());

            ResponseEntity<LocatarioResponseDTO> response = restTemplate.getForEntity(
                    Constants.LOCATARIO_ID_URL,
                    LocatarioResponseDTO.class,
                    locatario.getId()
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody().id(), locatario.getId());
        }

        @Test
        void deveRetornarErroQuandoLocatarioNaoExistir(){
            ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
                    Constants.LOCATARIO_ID_URL,
                    ErrorResponseDTO.class,
                    UUID.randomUUID()
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
        }
    }

    @Nested
    class BuscarLivrosLocatarioTests {
        @Test
        void deveBuscarLivrosAlugadosPorLocatarioComSucesso() {
            Aluguel aluguel = criarAluguel();

            ResponseEntity<List<LivroResumoDTO>> response = restTemplate.exchange(
                    Constants.LOCATARIO_LIVROS_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    aluguel.getLocatario().getId()
            );

            List<UUID> idLivrosEsperados = aluguel.getLivros().stream().map(Livro::getId).toList();
            List<UUID> idLivrosRetornados = response.getBody().stream().map(LivroResumoDTO::id).toList();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(idLivrosRetornados).hasSize(idLivrosEsperados.size());
            assertTrue(idLivrosRetornados.containsAll(idLivrosEsperados));
        }
    }

    private Aluguel criarAluguel(){
        Autor autor = autorRepository.save(AutorMock.getAutorMock());
        Livro livro = livroRepository.save(LivroMock.getLivroMock(autor));
        Locatario locatario = locatarioRepository.save(LocatarioMock.getLocatarioMock());

        Aluguel aluguel = AluguelMock.getAluguelMock(locatario.getId(), List.of(livro.getId()));

        return aluguelRepository.save(aluguel);
    }
}
