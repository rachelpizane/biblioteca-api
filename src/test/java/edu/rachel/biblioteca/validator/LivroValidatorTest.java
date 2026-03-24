package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.LivroAlugadoException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LivroValidatorTest {
    @Mock
    private LivroRepository repository;

    @Mock
    private AutorValidator autorValidator;

    @Mock
    private LocatarioValidator locatarioValidator;

    @InjectMocks
    private LivroValidator validator;

    @Nested
    class ValidarCadastroTests {
        private LivroRequestDTO request;

        @BeforeEach
        void setUp() {
            request = LivroMock.getLivroRequestDTOMock(List.of(UUID.randomUUID()));
        }


        @Test
        void deveLancarBusinessExceptionQuandoIsbnJaExistir() {
            when(repository.existsByIsbn(request.isbn())).thenReturn(true);

            assertThrows(BusinessException.class, () -> validator.validarCadastro(request));
        }

        @Test
        void deveLancarNotFoundExceptionQuandoAutorNaoExistir() {
            when(repository.existsByIsbn(request.isbn())).thenReturn(false);

            doThrow(new NotFoundException("Autor não encontrado"))
                    .when(autorValidator).validarExistencia(request.autoresIds());

            assertThrows(NotFoundException.class, () -> validator.validarCadastro(request));
        }

        @Test
        void naoDeveLancarExcecaoQuandoIsbnNaoExistirEAutoresExistirem() {
            when(repository.existsByIsbn(request.isbn())).thenReturn(false);
            doNothing().when(autorValidator).validarExistencia(request.autoresIds());

            assertDoesNotThrow(() -> validator.validarCadastro(request));
        }
    }

    @Nested
    class ValidarExistenciaTests {
        private UUID id1;
        private UUID id2;
        private List<UUID> ids;

        @BeforeEach
        void setUp() {
            id1 = UUID.randomUUID();
            id2 = UUID.randomUUID();

            ids = List.of(id1, id2);
        }

        @Test
        void deveLancarNotFoundExceptionQuandoAlgumLivroNaoExistir() {
            when(repository.findLivrosIdsByIdIn(ids)).thenReturn(List.of(id1));

            assertThrows(NotFoundException.class, () -> validator.validarExistencia(ids));
        }

        @Test
        void naoDeveLancarExcecaoQuandoTodosLivrosExistirem() {
            when(repository.findLivrosIdsByIdIn(ids)).thenReturn(List.of(id1, id2));

            assertDoesNotThrow(() -> validator.validarExistencia(ids));
        }
    }

    @Nested
    class ValidarAutorExistenteTests {
        private UUID autorId;

        @BeforeEach
        void setUp() {
            autorId = UUID.randomUUID();
        }

        @Test
        void deveLancarNotFoundExceptionQuandoAutorNaoExistir() {
            doThrow(new NotFoundException("Autor não encontrado"))
                    .when(autorValidator).validarExistencia(autorId);

            assertThrows(NotFoundException.class, () -> validator.validarAutorExistente(autorId));
        }

        @Test
        void naoDeveLancarExcecaoQuandoAutorExistir() {
            doNothing().when(autorValidator).validarExistencia(autorId);

            assertDoesNotThrow(() -> validator.validarAutorExistente(autorId));
        }
    }

    @Nested
    class ValidarLocatarioExistenteTests {
        private UUID locatarioId;

        @BeforeEach
        void setUp() {
            locatarioId = UUID.randomUUID();
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir() {
            doThrow(new NotFoundException("Locatário não encontrado"))
                    .when(locatarioValidator).validarExistencia(locatarioId);

            assertThrows(NotFoundException.class, () -> validator.validarLocatarioExistente(locatarioId));
        }

        @Test
        void naoDeveLancarExcecaoQuandoLocatarioExistir() {
            doNothing().when(locatarioValidator).validarExistencia(locatarioId);

            assertDoesNotThrow(() -> validator.validarLocatarioExistente(locatarioId));
        }
    }

    @Nested
    class ValidarLivrosDisponiveisTests {
        private UUID id1;
        private UUID id2;
        private List<UUID> ids;

        @BeforeEach
        void setUp() {
            id1 = UUID.randomUUID();
            id2 = UUID.randomUUID();

            ids = List.of(id1, id2);
        }

        @Test
        void deveLancarLivroAlugadoExceptionQuandoAlgumLivroEstiverAlugado() {
            when(repository.findLivrosIdsComAluguelPorStatus(ids, StatusEnum.EM_ANDAMENTO))
                    .thenReturn(List.of(id2));

            assertThrows(LivroAlugadoException.class, () -> validator.validarLivrosDisponiveis(ids));
        }

        @Test
        void naoDeveLancarExcecaoQuandoTodosLivrosEstiveremDisponiveis() {
            when(repository.findLivrosIdsComAluguelPorStatus(ids, StatusEnum.EM_ANDAMENTO))
                    .thenReturn(List.of());

            assertDoesNotThrow(() -> validator.validarLivrosDisponiveis(ids));
        }
    }
}