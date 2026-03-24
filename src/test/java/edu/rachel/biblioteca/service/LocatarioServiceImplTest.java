package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.ConflictBusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.LocatarioMapper;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.model.Locatario;
import edu.rachel.biblioteca.repository.AluguelRepository;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import edu.rachel.biblioteca.service.impl.LocatarioServiceImpl;
import edu.rachel.biblioteca.validator.LocatarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class LocatarioServiceImplTest {
    @Mock
    private LocatarioRepository locatarioRepository;
    
    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private LocatarioValidator validator;

    private LocatarioMapper mapper;

    private LocatarioServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper =  Mappers.getMapper(LocatarioMapper.class);
        service = new LocatarioServiceImpl(validator, mapper, locatarioRepository, aluguelRepository);
    }

    @Nested
    class CadastrarLocatarioTests {
        @Test
        void deveSalvarLocatarioCorretamente(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();
            Locatario locatario = LocatarioMock.getLocatarioMock(UUID.randomUUID());

            when(locatarioRepository.save(any(Locatario.class))).thenReturn(locatario);

            LocatarioResponseDTO response = service.cadastrarLocatario(request);

            assertEquals(response.id(), locatario.getId());
            verify(locatarioRepository, times(1)).save(any(Locatario.class));
        }

        @Test
        void deveLancarBusinessExceptionQuandoExistirLocatarioComCPFOuEmail(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            doThrow(new BusinessException("Já existe um locatário cadastrado com o CPF ou email informado"))
                    .when(validator).validarCadastro(request);

            assertThrows(BusinessException.class, () -> {
                service.cadastrarLocatario(request);
            });

            verify(locatarioRepository, never()).save(any(Locatario.class));
        }
    }

    @Nested
    class AtualizarLocatarioTests {
        @Test
        void deveAtualizarLocatarioCorretamente(){
            UUID locatarioId = UUID.randomUUID();

            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();
            Locatario locatario = LocatarioMock.getLocatarioMock(locatarioId);

            when(locatarioRepository.save(any(Locatario.class))).thenReturn(locatario);

            LocatarioResponseDTO response = service.atualizarLocatario(locatarioId, request);

            assertEquals(response.id(), locatario.getId());
            verify(locatarioRepository, times(1)).save(any(Locatario.class));
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir(){
            UUID idInvalid = UUID.randomUUID();
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            doThrow(new NotFoundException("Locatário não encontrado"))
                    .when(validator).validarAtualizacao(idInvalid, request);

            assertThrows(NotFoundException.class, () -> {
                service.atualizarLocatario(idInvalid, request);
            });

            verify(locatarioRepository, never()).save(any(Locatario.class));
        }

        @Test
        void deveLancarBusinessExceptionQuandoExistirLocatarioComCPFOuEmail(){
            UUID locatarioId = UUID.randomUUID();

            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            doThrow(new BusinessException("Já existe um locatário cadastrado com o CPF ou email informado"))
                    .when(validator).validarAtualizacao(locatarioId, request);

            assertThrows(BusinessException.class, () -> {
                service.atualizarLocatario(locatarioId, request);
            });

            verify(locatarioRepository, never()).save(any(Locatario.class));
        }
    }

    @Nested
    class BuscarLocatarioTests {
        @Test
        void deveBuscarLocatarioComSucesso() {
            Locatario locatario = LocatarioMock.getLocatarioMock(UUID.randomUUID());
            when(locatarioRepository.findById(locatario.getId())).thenReturn(Optional.of(locatario));

            LocatarioResponseDTO response = service.buscarLocatario(locatario.getId());

            assertEquals(locatario.getId(), response.id());
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir(){
            UUID idInvalid = UUID.randomUUID();

            when(locatarioRepository.findById(idInvalid)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                service.buscarLocatario(idInvalid);
            });
        }
    }

    @Nested
    class ExcluirLocatarioTests {
        @Test
        void deveExcluirLocatarioComSucesso(){
            Locatario locatario = LocatarioMock.getLocatarioMock(UUID.randomUUID());

            service.deletarLocatario(locatario.getId());

            verify(aluguelRepository, times(1)).deleteByLocatarioId(locatario.getId());
            verify(locatarioRepository, times(1)).deleteById(locatario.getId());
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir(){
            UUID idInvalid = UUID.randomUUID();

            doThrow(new NotFoundException("Locatário não encontrado"))
                    .when(validator).validarExclusao(idInvalid);

            assertThrows(NotFoundException.class, () -> {
                service.deletarLocatario(idInvalid);
            });

            verify(locatarioRepository, never()).save(any(Locatario.class));
        }

        @Test
        void deveLancarConflictBusinessExceptionQuandoLocatarioTiverAluguelEmAndamento(){
            Locatario locatario = LocatarioMock.getLocatarioMock(UUID.randomUUID());
            UUID locatarioId = locatario.getId();

            doThrow(new ConflictBusinessException("Locatário possui aluguéis em andamento"))
                    .when(validator).validarExclusao(locatarioId);

            assertThrows(ConflictBusinessException.class, () -> {
                service.deletarLocatario(locatarioId);
            });

            verify(locatarioRepository, never()).save(any(Locatario.class));
        }
    }
}
