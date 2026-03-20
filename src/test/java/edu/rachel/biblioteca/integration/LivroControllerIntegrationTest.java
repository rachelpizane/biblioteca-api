package edu.rachel.biblioteca.integration;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.enums.StatusLivroEnum;
import edu.rachel.biblioteca.mock.*;
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
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LivroControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private LivroRepository livroRepository;

    @MockitoSpyBean
    private AutorRepository autorRepository;

    @MockitoSpyBean
    private LocatarioRepository locatarioRepository;

    @MockitoSpyBean
    private AluguelRepository aluguelRepository;
    
    @AfterEach
    void tearDown() {
        aluguelRepository.deleteAll();
        locatarioRepository.deleteAll();
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
                    Constants.LIVRO_URL, request, LivroResponseDTO.class);

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

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(Constants.LIVRO_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("ISBN"));
            verify(livroRepository, times(1)).save(any(Livro.class));
        }

        @Test
        void deveRetornarErroQuandoAutorNaoEncontrado(){
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(List.of(UUID.randomUUID()));

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(Constants.LIVRO_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
            verify(livroRepository, never()).save(any(Livro.class));
        }
    }

    @Nested
    class BuscarLivroTests{
        @Test
        void deveBuscarLivroComSucesso(){
            Autor autor = autorRepository.save(AutorMock.getAutorMock());
            Livro livro = livroRepository.save(LivroMock.getLivroMock(autor));

            ResponseEntity<LivroResponseDTO> response = restTemplate.getForEntity(
                    Constants.LIVRO_ID_URL,
                    LivroResponseDTO.class,
                    livro.getId()
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody().id(), livro.getId());
            assertEquals(response.getBody().autores().getFirst().id(), autor.getId());
        }

        @Test
        void deveRetornarErroQuandoLivroNaoExistir(){
            ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
                    Constants.LIVRO_ID_URL,
                    ErrorResponseDTO.class,
                    UUID.randomUUID()
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
        }
    }

    @Nested
    class BuscarLivrosTests {
        Livro livro1;
        Livro livro2;

        @BeforeEach
        void setup() {
            Autor autor = autorRepository.save(AutorMock.getAutorMock());

            livro1 = criarLivro(autor, "2468135792468");
            livro2 = criarLivro(autor, "1357924681357");
        }

        @Test
        void deveBuscarTodosLivrosComSucesso() {
            List<UUID> livrosIds = List.of(livro1.getId(), livro2.getId());

            ResponseEntity<PageResponseDTO<LivroResumoDTO>> response = restTemplate.exchange(
                    Constants.LIVRO_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<UUID> idsRetornados = response.getBody().conteudo().stream().map(LivroResumoDTO::id).toList();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(idsRetornados).hasSize(livrosIds.size());
            assertTrue(idsRetornados.containsAll(livrosIds));

            verify(livroRepository, times(1)).findAll(PageMock.getPageableMock());
            verify(livroRepository, never()).findLivrosAlugados(PageMock.getPageableMock());
            verify(livroRepository, never()).findLivrosDisponiveis(PageMock.getPageableMock());
        }

        @Test
        void deveBuscarLivrosQuandoFiltradoPorStatus() {
            criarAluguel(livro1);

            String statusParam = "?status=" + StatusLivroEnum.ALUGADO;
            List<UUID> livrosIds = List.of(livro1.getId());

            ResponseEntity<PageResponseDTO<LivroResumoDTO>> response = restTemplate.exchange(
                    Constants.LIVRO_URL + statusParam,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<UUID> idsRetornados = response.getBody().conteudo().stream().map(LivroResumoDTO::id).toList();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(idsRetornados).hasSize(livrosIds.size());
            assertTrue(idsRetornados.containsAll(livrosIds));

            verify(livroRepository, never()).findAll(PageMock.getPageableMock());
            verify(livroRepository, times(1)).findLivrosAlugados(PageMock.getPageableMock());
            verify(livroRepository, never()).findLivrosDisponiveis(PageMock.getPageableMock());
        }
    }

    private void criarAluguel(Livro livro){
        Locatario locatario = locatarioRepository.save(LocatarioMock.getLocatarioMock());
        Aluguel aluguel = AluguelMock.getAluguelMock(locatario.getId(), List.of(livro.getId()));

        aluguelRepository.save(aluguel);
    }

    private Livro criarLivro(Autor autor,String isbn) {
        Livro livro = livroRepository.save(LivroMock.getLivroMock(autor));
        if (Objects.nonNull(isbn)) {
            livro.setIsbn(isbn);
        }
        return livroRepository.save(livro);
    }
}
