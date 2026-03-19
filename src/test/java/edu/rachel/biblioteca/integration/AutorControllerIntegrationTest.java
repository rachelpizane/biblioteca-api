package edu.rachel.biblioteca.integration;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.model.Livro;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
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
public class AutorControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private AutorRepository autorRepository;
    
    @MockitoSpyBean
    private LivroRepository livroRepository;

    public static final String AUTOR_URL = "/autores";
    public static final String AUTOR_ID_URL = AUTOR_URL  + "/{id}";
    public static final String AUTOR_LIVROS_URL = AUTOR_ID_URL + "/livros";

    @AfterEach
    void tearDown() {
        livroRepository.deleteAll();
        autorRepository.deleteAll();
    }
    @Nested
    class CadastrarAutorTests {
        @Test
        void deveCadastrarAutorCorretamente(){
            AutorRequestDTO request = AutorMock.getAutorRequestDTOMock();

            ResponseEntity<AutorResponseDTO> response = restTemplate.postForEntity(AUTOR_URL, request, AutorResponseDTO.class);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody().id());
            verify(autorRepository, times(1)).save(any(Autor.class));
        }

        @Test
        void deveRetornarErroQuandoExistirAutorCPF(){
            autorRepository.save(AutorMock.getAutorMock());

            AutorRequestDTO request = AutorMock.getAutorRequestDTOMock();

            ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(AUTOR_URL, request, ErrorResponseDTO.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("CPF"));
            verify(autorRepository, times(1)).save(any(Autor.class));
        }
    }

    @Nested
    class BuscarAutorTests{
        @Test
        void deveBuscarAutorComSucesso(){
            Autor autor = criarAutor();

            ResponseEntity<AutorResponseDTO> response = restTemplate.getForEntity(
                    AUTOR_ID_URL,
                    AutorResponseDTO.class,
                    autor.getId()
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(response.getBody().id(), autor.getId());
        }

        @Test
        void deveRetornarErroQuandoAutorNaoExistir(){
            ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
                    AUTOR_ID_URL,
                    ErrorResponseDTO.class,
                    UUID.randomUUID()
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
        }
    }

    @Nested
    class BuscarAutoresTests {
        Autor autor1;
        Autor autor2;

        @BeforeEach
        void setup() {
            autor1 = criarAutor();
            autor2 = criarAutor("Ana Maia", "24624047871");
        }

        @Test
        void deveBuscarTodosAutoresComSucesso(){
            List<UUID> autoresIds = List.of(autor1.getId(), autor2.getId());

            ResponseEntity<PageResponseDTO<AutorResumoDTO>> response = restTemplate.exchange(
                    AUTOR_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<UUID> idsRetornados = response.getBody().conteudo().stream().map(AutorResumoDTO::id).toList();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(idsRetornados).hasSize(autoresIds.size());
            assertTrue(idsRetornados.containsAll(autoresIds));
        }

        @Test
        void deveBuscarAutoresQuandoFiltradoPeloNome(){
            String parametro = "?nome=maia";

            ResponseEntity<PageResponseDTO<AutorResumoDTO>> response = restTemplate.exchange(
                    AUTOR_URL + parametro,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<UUID> idsRetornados = response.getBody().conteudo().stream().map(AutorResumoDTO::id).toList();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(idsRetornados).hasSize(1);
            assertTrue(idsRetornados.contains(autor2.getId()));
        }
    }
    
    @Nested
    class BuscarLivrosAutorTests {
        @Test
        void deveBuscarLivrosAutorCorretamente(){
            Autor autor = criarAutor();
            Livro livro = criarLivro(autor);

            ResponseEntity<List<LivroResumoDTO>> response = restTemplate.exchange(
                    AUTOR_LIVROS_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    autor.getId()
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).hasSize(1);
            assertEquals(response.getBody().getFirst().id(), livro.getId());
        }

        @Test
        void deveRetornarErroQuandoAutorNaoExistir(){
            ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
                    AUTOR_LIVROS_URL,
                    ErrorResponseDTO.class,
                    UUID.randomUUID()
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.getBody().mensagens().getFirst().contains("não encontrado"));
        }
    }

    public Autor criarAutor() {
        return criarAutor(null, null);
    }
    
    public Autor criarAutor(String nome, String cpf) {
        Autor autor = AutorMock.getAutorMock();

        if(Objects.nonNull(nome)) {
            autor.setNome(nome);
        }

        if(Objects.nonNull(cpf)) {
            autor.setCpf(cpf);
        }

        return autorRepository.save(autor);
    }

    private Livro criarLivro(Autor autor) {
        return livroRepository.save(LivroMock.getLivroMock(autor));
    }
}
