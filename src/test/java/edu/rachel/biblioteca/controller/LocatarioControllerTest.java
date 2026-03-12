package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.controller.impl.LocatarioController;
import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.service.LocatarioService;
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
import java.util.stream.Stream;

import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocatarioController.class)
class LocatarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocatarioService locatarioService;

    public static final String LOCATARIO_URL = "/locatarios";

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

            mockMvc.perform(post(LOCATARIO_URL)
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
}
