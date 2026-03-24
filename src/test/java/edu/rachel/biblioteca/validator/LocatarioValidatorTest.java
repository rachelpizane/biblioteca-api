package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.ConflictBusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.repository.AluguelRepository;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocatarioValidatorTest {
    @Mock
    private LocatarioRepository locatarioRepository;

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private AluguelValidator aluguelValidator;

    @InjectMocks
    private LocatarioValidator validator;

    @Nested
    class ValidarCadastroTests {
        private LocatarioRequestDTO request;

        @BeforeEach
        void setUp() {
            request = LocatarioMock.getLocatarioRequestDTOMock();
        }

        @Test
        void deveLancarBusinessExceptionQuandoCpfExistir() {
            when(locatarioRepository.existsByCpfAndIdNotNullable(request.cpf(), null)).thenReturn(true);

            assertThrows(BusinessException.class, () -> validator.validarCadastro(request));
        }

        @Test
        void deveLancarBusinessExceptionQuandoEmailExistir() {
            when(locatarioRepository.existsByCpfAndIdNotNullable(request.cpf(), null)).thenReturn(false);
            when(locatarioRepository.existsByEmailIgnoreCaseAndIdNotNullable(request.email(), null)).thenReturn(true);

            assertThrows(BusinessException.class, () -> validator.validarCadastro(request));
        }

        @Test
        void naoDeveLancarExcecaoQuandoCpfEEmailNaoExistirem() {
            when(locatarioRepository.existsByCpfAndIdNotNullable(request.cpf(), null)).thenReturn(false);
            when(locatarioRepository.existsByEmailIgnoreCaseAndIdNotNullable(request.email(), null)).thenReturn(false);

            assertDoesNotThrow(() -> validator.validarCadastro(request));
        }
    }

    @Nested
    class ValidarAtualizacaoTests {
        private LocatarioRequestDTO request;
        private UUID id;

        @BeforeEach
        void setUp() {
            request = LocatarioMock.getLocatarioRequestDTOMock();
            id = UUID.randomUUID();
        }

        @Test
        void deveLancarBusinessExceptionQuandoCpfExistirParaOutroId() {
            when(locatarioRepository.existsById(id)).thenReturn(true);
            when(locatarioRepository.existsByCpfAndIdNotNullable(request.cpf(), id)).thenReturn(true);

            assertThrows(BusinessException.class, () -> validator.validarAtualizacao(id, request));
        }

        @Test
        void deveLancarBusinessExceptionQuandoEmailExistirParaOutroId() {
            when(locatarioRepository.existsById(id)).thenReturn(true);
            when(locatarioRepository.existsByCpfAndIdNotNullable(request.cpf(), id)).thenReturn(false);
            when(locatarioRepository.existsByEmailIgnoreCaseAndIdNotNullable(request.email(), id)).thenReturn(true);

            assertThrows(BusinessException.class, () -> validator.validarAtualizacao(id, request));
        }

        @Test
        void naoDeveLancarExcecaoQuandoCpfEEmailNaoExistiremParaOutroId() {
            when(locatarioRepository.existsById(id)).thenReturn(true);
            when(locatarioRepository.existsByCpfAndIdNotNullable(request.cpf(), id)).thenReturn(false);
            when(locatarioRepository.existsByEmailIgnoreCaseAndIdNotNullable(request.email(), id)).thenReturn(false);

            assertDoesNotThrow(() -> validator.validarAtualizacao(id, request));
        }
    }

    @Nested
    class ValidarExclusaoTests {
        private UUID id;

        @BeforeEach
        void setUp() {
            id = UUID.randomUUID();
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir() {
            when(locatarioRepository.existsById(id)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> validator.validarExclusao(id));
        }

        @Test
        void deveLancarConflictBusinessExceptionQuandoLocatarioTiverAluguelEmAndamento() {

            when(locatarioRepository.existsById(id)).thenReturn(true);
            when(aluguelRepository.existsByLocatarioIdAndStatus(id, StatusEnum.EM_ANDAMENTO))
                    .thenReturn(true);

            assertThrows(ConflictBusinessException.class, () -> validator.validarExclusao(id));
        }

        @Test
        void naoDeveLancarExcecaoQuandoValidadoresNaoLancam() {
            when(locatarioRepository.existsById(id)).thenReturn(true);
            when(aluguelRepository.existsByLocatarioIdAndStatus(id, StatusEnum.EM_ANDAMENTO))
                    .thenReturn(false);

            assertDoesNotThrow(() -> validator.validarExclusao(id));
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
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir() {
            when(locatarioRepository.existsById(id)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> validator.validarExistencia(id));
        }

        @Test
        void naoDeveLancarExcecaoQuandoLocatarioExistir() {
            when(locatarioRepository.existsById(id)).thenReturn(true);

            assertDoesNotThrow(() -> validator.validarExistencia(id));
        }
    }
}