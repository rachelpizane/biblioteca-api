package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.controller.impl.AutorController;
import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.enums.SexoEnum;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.service.AutorService;
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

import java.util.stream.Stream;

import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
class AutorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutorService autorService;

    public static final String AUTOR_URL = "/autores";

    @Nested
    class CadastrarAutorTests {
        @Test
        void deveCriarAutorCorretamente() throws Exception {
            AutorRequestDTO request = AutorMock.getAutorRequestDTOMock();
            AutorResponseDTO response = AutorMock.getAutorResponseDTOMock();

            when(autorService.criarAutor(request)).thenReturn(response);

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
}