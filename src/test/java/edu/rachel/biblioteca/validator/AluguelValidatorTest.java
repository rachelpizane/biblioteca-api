package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.LivroAlugadoException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.exception.StatusInvalidoException;
import edu.rachel.biblioteca.mock.AluguelMock;
import edu.rachel.biblioteca.model.Aluguel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AluguelValidatorTest {

    @Mock
    private LocatarioValidator locatarioValidator;

    @Mock
    private LivroValidator livroValidator;

    @InjectMocks
    private AluguelValidator validator;

    @Nested
    class ValidarCadastroTests {
        private AluguelRequestDTO request;

        @BeforeEach
        void setUp() {
            request = AluguelMock
                    .getAluguelRequestDTOMock(UUID.randomUUID(), List.of(UUID.randomUUID()));
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir() {
            doThrow(new NotFoundException("Locatário não encontrado"))
                    .when(locatarioValidator).validarExistencia(request.locatarioId());

            assertThrows(NotFoundException.class, () -> validator.validarCadastro(request));
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLivroNaoExistir() {
            doNothing().when(locatarioValidator).validarExistencia(request.locatarioId());
            doThrow(new NotFoundException("Livros não encontrados"))
                    .when(livroValidator).validarExistencia(request.livrosIds());


            assertThrows(NotFoundException.class, () -> validator.validarCadastro(request));
        }

        @Test
        void deveLancarLivroAlugadoExceptionQuandoLivroNãoEstiverDisponivel() {
            doNothing().when(locatarioValidator).validarExistencia(request.locatarioId());
            doNothing().when(livroValidator).validarExistencia(request.livrosIds());
            doThrow(new LivroAlugadoException("Livro alugado"))
                    .when(livroValidator).validarLivrosDisponiveis(request.livrosIds());

            assertThrows(LivroAlugadoException.class, () -> validator.validarCadastro(request));
        }

        @Test
        void naoDeveLancarExcecaoQuandoValidadoresNaoLancam() {
            doNothing().when(locatarioValidator).validarExistencia(request.locatarioId());
            doNothing().when(livroValidator).validarExistencia(request.livrosIds());
            doNothing().when(livroValidator).validarLivrosDisponiveis(request.livrosIds());

            assertDoesNotThrow(() -> validator.validarCadastro(request));
        }
    }

    @Nested
    class ValidarAtualizacaoStatusTests {
        private Aluguel aluguel;

        @BeforeEach
        void setUp() {
            aluguel = AluguelMock.getAluguelMock(UUID.randomUUID(), List.of(UUID.randomUUID()));
        }

        @ParameterizedTest
        @EnumSource(value = StatusEnum.class, names = {"FINALIZADO", "CANCELADO"})
        void naoDeveLancarExcecaoQuandoAtualizacaoValida(StatusEnum statusNovo) {
            assertDoesNotThrow(() -> validator.validarAtualizacaoStatus(aluguel, statusNovo));
        }

        @ParameterizedTest
        @MethodSource("statusInvalidos")
        void deveLancarStatusInvalidoExceptionQuandoStatusInvalido(StatusEnum statusAtual, StatusEnum statusNovo) {
            aluguel.setStatus(statusAtual);

            assertThrows(StatusInvalidoException.class, () -> validator.validarAtualizacaoStatus(aluguel, statusNovo));
        }

        static Stream<Arguments> statusInvalidos() {
            return Stream.of(
                    Arguments.of(StatusEnum.FINALIZADO, StatusEnum.FINALIZADO),
                    Arguments.of(StatusEnum.FINALIZADO, StatusEnum.CANCELADO),
                    Arguments.of(StatusEnum.CANCELADO, StatusEnum.CANCELADO),
                    Arguments.of(StatusEnum.CANCELADO, StatusEnum.FINALIZADO),
                    Arguments.of(StatusEnum.CANCELADO, StatusEnum.EM_ANDAMENTO),
                    Arguments.of(StatusEnum.FINALIZADO, StatusEnum.EM_ANDAMENTO)
            );
        }
    }
}