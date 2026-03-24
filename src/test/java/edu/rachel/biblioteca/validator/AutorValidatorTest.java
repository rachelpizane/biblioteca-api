package edu.rachel.biblioteca.validator;


import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.repository.AutorRepository;
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
class AutorValidatorTest {

    @Mock
    private AutorRepository repository;

    @InjectMocks
    private AutorValidator validator;

    @Nested
    class ValidarCadastroTests {
        private AutorRequestDTO request;

        @BeforeEach
        void setUp() {
            request = AutorMock.getAutorRequestDTOMock();
        }

        @Test
        void deveLancarBusinessExceptionQuandoCpfJaExistir() {
            when(repository.existsByCpf(request.cpf())).thenReturn(true);

            assertThrows(BusinessException.class, () -> validator.validarCadastro(request));
        }

        @Test
        void naoDeveLancarExcecaoQuandoCpfNaoExistir() {
            when(repository.existsByCpf(request.cpf())).thenReturn(false);

            assertDoesNotThrow(() -> validator.validarCadastro(request));
        }
    }

    @Nested
    class ValidarExistenciaPorIdTests {
        private UUID id;

        @BeforeEach
        void setUp() {
            id = UUID.randomUUID();
        }

        @Test
        void deveLancarNotFoundExceptionQuandoAutorNaoExistir() {
            when(repository.existsById(id)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> validator.validarExistencia(id));
        }

        @Test
        void naoDeveLancarExcecaoQuandoAutorExistir() {
            when(repository.existsById(id)).thenReturn(true);

            assertDoesNotThrow(() -> validator.validarExistencia(id));
            verify(repository, times(1)).existsById(id);
        }
    }

    @Nested
    class ValidarExistenciaPorListaIdsTests {
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
        void deveLancarNotFoundExceptionQuandoAlgumAutorNaoExistir() {
            when(repository.findAllById(ids)).thenReturn(List.of(AutorMock.getAutorMock(id1)));

            assertThrows(NotFoundException.class, () -> validator.validarExistencia(ids));
        }

        @Test
        void naoDeveLancarExcecaoQuandoTodosAutoresExistirem() {
            when(repository.findAllById(ids)).thenReturn(
                    List.of(
                            AutorMock.getAutorMock(id1),
                            AutorMock.getAutorMock(id2)
                    )
            );

            assertDoesNotThrow(() -> validator.validarExistencia(ids));
        }
    }
}