package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.controller.impl.LocatarioController;
import edu.rachel.biblioteca.dto.LivroResumoDTO;
import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.service.LivroService;
import edu.rachel.biblioteca.service.LocatarioService;
import edu.rachel.biblioteca.utils.Constants;
import edu.rachel.biblioteca.utils.JsonUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static edu.rachel.biblioteca.utils.Constants.LOCATARIO_URL;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocatarioController.class)
class LocatarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocatarioService locatarioService;

    @MockitoBean
    private LivroService livroService;
    
    @Nested
    class CadastrarLocatarioTests {
        @Test
        void deveCadastrarLocatarioCorretamente() throws Exception {
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();
            LocatarioResponseDTO response = LocatarioMock.getLocatarioResponseDTOMock();

            when(locatarioService.cadastrarLocatario(request)).thenReturn(response);

            mockMvc.perform(post(LOCATARIO_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @ParameterizedTest
        @MethodSource("requestInvalidos")
        void deveRetornarBadRequestParaDadosInvalidos(LocatarioRequestDTO requestInvalido) throws Exception {

            mockMvc.perform(post(Constants.LOCATARIO_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(requestInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }

        static Stream<LocatarioRequestDTO> requestInvalidos() {
            return Stream.of(
                    LocatarioMock.getRequestComCpf("1234567890"),
                    LocatarioMock.getRequestComCpf("1234567890T"),
                    LocatarioMock.getRequestComNome(""),
                    LocatarioMock.getRequestComEmail("email-invalido"),
                    LocatarioMock.getRequestComTelefone("1199999999"),
                    LocatarioMock.getRequestComTelefone("1199999999T"),
                    LocatarioMock.getRequestComDataNascimento(LocalDate.now().plusDays(1))
            );
        }

        @Test
        void deveRetornarBadRequestParaEnumInvalido() throws Exception {
            String requestJson = """
            {
              "cpf": "10987654321",
              "nome": "Maria Oliveira",
              "sexo": "INVALIDO",
              "email": "maria.oliveira@email.com",
              "telefone": "11998765432",
              "dataNascimento": "1990-05-15"
            }
            """;

            mockMvc.perform(post(LOCATARIO_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }

    @Nested
    class BuscarLocatarioTests {
        @Test
        void deveBuscarLocatarioComSucesso() throws Exception {
            LocatarioResponseDTO response = LocatarioMock.getLocatarioResponseDTOMock();

            when(locatarioService.buscarLocatario(response.id())).thenReturn(response);

            mockMvc.perform(get(Constants.LOCATARIO_ID_URL, response.id())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @Test
        void deveRetornarNotFoundQuandoLocatarioNaoExistir() throws Exception {
            UUID idInvalid = UUID.randomUUID();

            when(locatarioService.buscarLocatario(idInvalid))
                    .thenThrow(new NotFoundException("Locatário não encontrado"));

            mockMvc.perform(get(Constants.LOCATARIO_ID_URL, idInvalid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }

    @Nested
    class BuscarLivrosLocatarioTests {

        @Test
        void deveBuscarLivrosAlugadosPorLocatarioComSucesso() throws Exception {
            UUID locatarioId = UUID.randomUUID();
            List<LivroResumoDTO> livros = List.of(LivroMock.getLivroResumoDTOMock());

            when(livroService.buscarLivrosPorLocatario(locatarioId)).thenReturn(livros);

            mockMvc.perform(get(Constants.LOCATARIO_LIVROS_URL, locatarioId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(livros)));
        }

        @Test
        void deveRetornarNotFoundQuandoLocatarioNaoExistir() throws Exception {
            UUID idInvalid = UUID.randomUUID();

            when(livroService.buscarLivrosPorLocatario(idInvalid))
                    .thenThrow(new NotFoundException("Locatário não encontrado"));

            mockMvc.perform(get(Constants.LOCATARIO_LIVROS_URL, idInvalid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }
}
