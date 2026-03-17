package edu.rachel.biblioteca.controller;

import edu.rachel.biblioteca.controller.impl.AluguelController;
import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;
import edu.rachel.biblioteca.dto.StatusRequestDTO;
import edu.rachel.biblioteca.dto.StatusResponseDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.exception.StatusInvalidoException;
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
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AluguelController.class)
class AluguelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AluguelService aluguelService;

    public static final String ALUGUEL_URL = "/alugueis";
    public static final String ALUGUEL_ID_URL = ALUGUEL_URL + "/{id}";
    public static final String ALUGUEL_STATUS_URL = ALUGUEL_ID_URL + "/status";

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

    @Nested
    class BuscarAluguelTests {
        @Test
        void deveBuscarAluguelComSucesso() throws Exception {
            AluguelResponseDTO response = AluguelMock.getAluguelResponseDTOMock();

            when(aluguelService.buscarAluguel(response.id())).thenReturn(response);

            mockMvc.perform(get(ALUGUEL_ID_URL, response.id())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @Test
        void deveRetornarNotFoundQuandoAluguelNaoExistir() throws Exception {
            UUID idInvalid = UUID.randomUUID();

            when(aluguelService.buscarAluguel(idInvalid)).thenThrow(new NotFoundException("Aluguel não encontrado"));

            mockMvc.perform(get(ALUGUEL_ID_URL, idInvalid)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }

    @Nested
    class AtualizarStatusAluguelTests {
        @Test
        void deveAtualizarAluguelCorretamente() throws Exception {
            UUID idAluguel = UUID.randomUUID();
            StatusEnum statusNovo = StatusEnum.FINALIZADO;

            StatusRequestDTO request= new StatusRequestDTO(statusNovo);
            StatusResponseDTO response = new StatusResponseDTO(idAluguel, statusNovo);

            when(aluguelService.atualizarStatusAluguel(idAluguel, request.status())).thenReturn(response);

            mockMvc.perform(
                    patch(ALUGUEL_STATUS_URL, idAluguel)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.convertToJson(request)
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().json(JsonUtils.convertToJson(response)));
        }

        @Test
        void deveRetornarBadRequestQuandoRequestInvalido() throws Exception {
            UUID idAluguel = UUID.randomUUID();
            StatusRequestDTO request= new StatusRequestDTO(null);

            mockMvc.perform(
                            patch(ALUGUEL_STATUS_URL, idAluguel)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.convertToJson(request)
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }

        @Test
        void deveRetornarConflictQuandoStatusInvalido() throws Exception {
            UUID idInvalid = UUID.randomUUID();
            StatusRequestDTO request= new StatusRequestDTO(StatusEnum.FINALIZADO);

            when(aluguelService.atualizarStatusAluguel(idInvalid, request.status()))
                    .thenThrow(new StatusInvalidoException("Não é possível alterar status"));

            mockMvc.perform(
                            patch(ALUGUEL_STATUS_URL, idInvalid)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonUtils.convertToJson(request)
                                    )
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.mensagens.length()").value(greaterThan(0)));
        }
    }
}
