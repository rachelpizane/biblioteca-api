package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.controller.impl.LivroController;
import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.enums.StatusLivroEnum;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.mock.PageMock;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
public class LivroControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LivroService livroService;

    public static final String LIVRO_URL = "/livros";

    @Nested
    class CadastrarLivroTests {
        @Test
        void deveCadastrarLivroCorretamente() throws Exception {
            UUID idAutor = UUID.randomUUID();
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(List.of(idAutor));
            LivroResponseDTO response = LivroMock.getLivroResponseDTOMock(idAutor);

            when(livroService.cadastrarLivro(request)).thenReturn(response);

            mockMvc.perform(post(LIVRO_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @ParameterizedTest
        @MethodSource("requestInvalidos")
        void deveRetornarBadRequestParaDadosInvalidos(LivroRequestDTO requestInvalido) throws Exception {

            mockMvc.perform(post(LIVRO_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(requestInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }

        static Stream<LivroRequestDTO> requestInvalidos() {
            return Stream.of(
                    new LivroRequestDTO(
                            "",
                            "1234567890123",
                            LocalDate.of(2022, 11, 21),
                            List.of(UUID.randomUUID())
                    ),
                    new LivroRequestDTO(
                            "Livro Teste",
                            "1234567890123T",
                            LocalDate.of(2022, 11, 21),
                            List.of(UUID.randomUUID())
                    ),
                    new LivroRequestDTO(
                            "Livro Teste",
                            "1234567890123",
                            null,
                            List.of(UUID.randomUUID())
                    ),
                    new LivroRequestDTO(
                            "Livro Teste",
                            "1234567890123",
                            LocalDate.of(2022, 11, 21),
                            List.of()
                    )
            );
        }

        @Test
        void deveRetornarNotFoundQuandoAutorNaoEncontrado() throws Exception {
            UUID idAutor = UUID.randomUUID();
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(List.of(idAutor));

            when(livroService.cadastrarLivro(request))
                    .thenThrow(new NotFoundException("Autores não encontrados"));

            mockMvc.perform(post(LIVRO_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }

    @Nested
    class BuscarLivroTests {
        @Test
        void deveBuscarLivroComSucesso() throws Exception {
            LivroResponseDTO response = LivroMock.getLivroResponseDTOMock(UUID.randomUUID());

            when(livroService.buscarLivro(response.id())).thenReturn(response);

            mockMvc.perform(get(LIVRO_URL + "/{id}", response.id())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @Test
        void deveRetornarNotFoundQuandoLivroNaoExistir() throws Exception {
            UUID idInvalid = UUID.randomUUID();

            when(livroService.buscarLivro(idInvalid)).thenThrow(new NotFoundException("Livro não encontrado"));

            mockMvc.perform(get(LIVRO_URL + "/{id}", idInvalid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }

    @Nested
    class BuscarLivrosTests {
        @ParameterizedTest
        @MethodSource("parametrosProvider")
        void deveBuscarLivrosComOuSemFiltro(StatusLivroEnum statusParam) throws Exception {
            String url = Objects.isNull(statusParam) ? LIVRO_URL : LIVRO_URL + "?status=" + statusParam;
            List<LivroResumoDTO> livros = List.of(LivroMock.getLivroResumoDTOMock(), LivroMock.getLivroResumoDTOMock());

            Pageable pageable = PageMock.getPageableMock();
            PageResponseDTO page = PageMock.getPageResponseDTOMock(livros);

            when(livroService.buscarLivros(statusParam, pageable)).thenReturn(page);

            mockMvc.perform(get(url)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(page)));
        }

        static Stream<StatusLivroEnum> parametrosProvider() {
            return Stream.of(
                    null,
                    StatusLivroEnum.DISPONIVEL
            );
        }
    }
}
