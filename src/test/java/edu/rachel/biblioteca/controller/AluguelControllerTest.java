package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.controller.impl.AluguelController;
import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;
import edu.rachel.biblioteca.mock.AluguelMock;
import edu.rachel.biblioteca.service.AluguelService;
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
import java.util.stream.Stream;

import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AluguelController.class)
class AluguelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AluguelService aluguelService;

    public static final String ALUGUEL_URL = "/alugueis";

    @Nested
    class CadastrarAluguelTests {
        @Test
        void deveCadastrarAluguelCorretamente() throws Exception {
            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock();
            AluguelResponseDTO response = AluguelMock.getAluguelResponseDTOMock();

            when(aluguelService.cadastrarAluguel(request)).thenReturn(response);

            mockMvc.perform(post(ALUGUEL_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @ParameterizedTest
        @MethodSource("requestInvalidos")
        void deveRetornarBadRequestParaDadosInvalidos(AluguelRequestDTO requestInvalido) throws Exception {

            mockMvc.perform(post(ALUGUEL_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(requestInvalido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }

        static Stream<AluguelRequestDTO> requestInvalidos() {
            return Stream.of(
                    AluguelMock.getRequestComDataRetiradaEDevolucao(null, null),
                    AluguelMock.getRequestComDataRetiradaEDevolucao(LocalDate.now().plusDays(1), null),
                    AluguelMock.getRequestComDataRetiradaEDevolucao(LocalDate.now(), LocalDate.now().minusDays(1)),
                    AluguelMock.getRequestComLocatarioId(null),
                    AluguelMock.getRequestComLivrosIds(null),
                    AluguelMock.getRequestComLivrosIds(List.of())
            );
        }
    }
}
