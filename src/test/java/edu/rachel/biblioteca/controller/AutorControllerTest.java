package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.controller.impl.AutorController;
import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.enums.SexoEnum;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.mock.PageMock;
import edu.rachel.biblioteca.service.AutorService;
import edu.rachel.biblioteca.service.LivroService;
import edu.rachel.biblioteca.utils.JsonUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
class AutorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutorService autorService;

    @MockitoBean
    private LivroService livroService;

    public static final String AUTOR_URL = "/autores";
    public static final String AUTOR_ID_URL = AUTOR_URL  + "/{id}";
    public static final String AUTOR_LIVROS_URL = AUTOR_ID_URL + "/livros";

    @Nested
    class CadastrarAutorTests {
        @Test
        void deveCadastrarAutorCorretamente() throws Exception {
            AutorRequestDTO request = AutorMock.getAutorRequestDTOMock();
            AutorResponseDTO response = AutorMock.getAutorResponseDTOMock();

            when(autorService.cadastrarAutor(request)).thenReturn(response);

            mockMvc.perform(post(AUTOR_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @ParameterizedTest
        @MethodSource("requestInvalidos")
        void deveRetornarBadRequestParaDadosInvalidos(AutorRequestDTO requestInvalido) throws Exception {

            mockMvc.perform(post(AUTOR_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(requestInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }

        static Stream<AutorRequestDTO> requestInvalidos() {
            return Stream.of(
                    new AutorRequestDTO("12345678910T", "Carlos Silva", SexoEnum.MASCULINO, 1986) ,
                    new AutorRequestDTO("12345678910", "", SexoEnum.MASCULINO, 1986),
                    new AutorRequestDTO("12345678910", "Carlos Silva", SexoEnum.MASCULINO, -1)
            );
        }

        @Test
        void deveRetornarBadRequestParaEnumInvalido() throws Exception {
            String requestJson = """
            {
                "cpf": "12345678910",
                "nome": "Carlos Silva",
                "sexo": "INVALIDO",
                "anoNascimento": 1986
            }
            """;

            mockMvc.perform(post(AUTOR_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }

    @Nested
    class BuscarAutorTests {
        @Test
        void deveBuscarAutorComSucesso() throws Exception {
            AutorResponseDTO response = AutorMock.getAutorResponseDTOMock();

            when(autorService.buscarAutor(response.id())).thenReturn(response);

            mockMvc.perform(get(AUTOR_ID_URL, response.id())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @Test
        void deveRetornarNotFoundQuandoAutorNaoExistir() throws Exception {
            UUID idInvalid = UUID.randomUUID();

            when(autorService.buscarAutor(idInvalid)).thenThrow(new NotFoundException("Autor não encontrado"));

            mockMvc.perform(get(AUTOR_ID_URL, idInvalid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }

    @Nested
    class BuscarAutoresTests {

        @ParameterizedTest
        @MethodSource("parametrosProviders")
        void deveBuscarAutoresComSucesso(String nomeParam) throws Exception {
            String url = Objects.isNull(nomeParam) ? AUTOR_URL : AUTOR_URL + "?nome=" + nomeParam;
            List<AutorResumoDTO> autores = List.of(AutorMock.getAutorResumoDTOMock(), AutorMock.getAutorResumoDTOMock());

            Pageable pageable = PageMock.getPageableMock();
            PageResponseDTO page = PageMock.getPageResponseDTOMock(autores);

            when(autorService.buscarAutores(nomeParam, pageable)).thenReturn(page);

            mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(page)));
        }

        static Stream<String> parametrosProviders() {
            return Stream.of(
                    null,
                    "carlos"
            );
        }
    }

    @Nested
    class BuscarLivrosAutorTests {
        @Test
        void deveBuscarLivrosAutorComSucesso() throws Exception {
            UUID autorId = UUID.randomUUID();
            List<LivroResumoDTO> livros = List.of(LivroMock.getLivroResumoDTOMock());

            when(livroService.buscarLivrosPorAutor(autorId)).thenReturn(livros);

            mockMvc.perform(get(AUTOR_LIVROS_URL, autorId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(livros)));
        }

        @Test
        void deveRetornarNotFoundQuandoAutorNaoExistir() throws Exception {
            UUID idInvalid = UUID.randomUUID();

            when(livroService.buscarLivrosPorAutor(idInvalid))
                    .thenThrow(new NotFoundException("Autor não encontrado"));

            mockMvc.perform(get(AUTOR_LIVROS_URL, idInvalid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }
}