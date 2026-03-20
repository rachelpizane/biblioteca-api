package edu.rachel.biblioteca.integration;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.enums.StatusEnum;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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
                    Constants.ALUGUEL_URL, request, AluguelResponseDTO.class);

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

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(Constants.ALUGUEL_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("aluguel em andamento"));
            verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        }

        @Test
        void deveRetornarErroQuandoLocatarioNaoForEncontrado(){
            Autor autor = autorRepository.save(AutorMock.getAutorMock());
            Livro livro = livroRepository.save(LivroMock.getLivroMock(autor));

            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(UUID.randomUUID(), List.of(livro.getId()));

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(Constants.ALUGUEL_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("Locatário não encontrado"));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        void deveRetornarErroQuandoLivroNaoForEncontrado(){
            Locatario locatario = locatarioRepository.save(LocatarioMock.getLocatarioMock());

            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(locatario.getId(), List.of(UUID.randomUUID()));

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(Constants.ALUGUEL_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("Livros não encontrados"));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }
    }

    @Nested
    class BuscarAluguelTests{
        @Test
        void deveBuscarAluguelComSucesso(){
            Aluguel aluguel = criarAluguel();

            ResponseEntity<AluguelResponseDTO> response = restTemplate.getForEntity(
                    Constants.ALUGUEL_ID_URL,
                    AluguelResponseDTO.class,
                    aluguel.getId()
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody().id(), aluguel.getId());
        }

        @Test
        void deveRetornarErroQuandoAluguelNaoExistir(){
            ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
                    Constants.ALUGUEL_ID_URL,
                    ErrorResponseDTO.class,
                    UUID.randomUUID()
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
        }
    }

    @Nested
    class AtualizarStatusAluguelTests {
        @Test
        void deveAtualizarAluguelCorretamente(){
            StatusEnum statusNovo = StatusEnum.FINALIZADO;
            Aluguel aluguel = criarAluguel();

            StatusRequestDTO body = new StatusRequestDTO(statusNovo);
            HttpEntity<StatusRequestDTO> request = new HttpEntity<>(body);

            ResponseEntity<StatusResponseDTO> response = restTemplate.exchange(
                    Constants.ALUGUEL_STATUS_URL,
                    HttpMethod.PATCH,
                    request,
                    StatusResponseDTO.class,
                    aluguel.getId()
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(aluguel.getId(), response.getBody().idAluguel());
            assertEquals(statusNovo, response.getBody().status());
            verify(aluguelRepository, times(2)).save(any(Aluguel.class));
        }

        @Test
        void deveRetornarErroQuandoAluguelNaoEncontrado(){
            StatusRequestDTO body = new StatusRequestDTO(StatusEnum.FINALIZADO);
            HttpEntity<StatusRequestDTO> request = new HttpEntity<>(body);

            ResponseEntity<ErrorResponseDTO> response = restTemplate.exchange(
                    Constants.ALUGUEL_STATUS_URL,
                    HttpMethod.PATCH,
                    request,
                    ErrorResponseDTO.class,
                    UUID.randomUUID()
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("Aluguel não encontrado"));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @ParameterizedTest
        @MethodSource("statusInvalidos")
        void deveRetornarConflitoParaStatusInvalidos(StatusEnum statusAtual, StatusEnum statusNovo, String mensagemEsperada) {
            Aluguel aluguel = criarAluguel(statusAtual);

            StatusRequestDTO body = new StatusRequestDTO(statusNovo);
            HttpEntity<StatusRequestDTO> request = new HttpEntity<>(body);

            ResponseEntity<ErrorResponseDTO> response = restTemplate.exchange(
                    Constants.ALUGUEL_STATUS_URL,
                    HttpMethod.PATCH,
                    request,
                    ErrorResponseDTO.class,
                    aluguel.getId()
            );

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains(mensagemEsperada));
            verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        }

        static Stream<Arguments> statusInvalidos() {
            return Stream.of(
                    Arguments.of(StatusEnum.FINALIZADO, StatusEnum.CANCELADO, "Não é possível alterar o status"),
                    Arguments.of(StatusEnum.FINALIZADO, StatusEnum.EM_ANDAMENTO, "Não é permitido reabrir um aluguel")
            );
        }
    }

    private Aluguel criarAluguel(){
        return criarAluguel(StatusEnum.EM_ANDAMENTO);
    }

    private Aluguel criarAluguel(StatusEnum status){
        Autor autor = autorRepository.save(AutorMock.getAutorMock());
        Livro livro = livroRepository.save(LivroMock.getLivroMock(autor));
        Locatario locatario = locatarioRepository.save(LocatarioMock.getLocatarioMock());

        Aluguel aluguel = AluguelMock.getAluguelMock(locatario.getId(), List.of(livro.getId()));
        aluguel.setStatus(status);

        return aluguelRepository.save(aluguel);
    }
}
