package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.ConflictBusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.LocatarioMapper;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.model.Aluguel;
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
import org.mockito.ArgumentCaptor;
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
    
    private LocatarioMapper mapper;

    private LocatarioValidator validator;

    private LocatarioServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper =  Mappers.getMapper(LocatarioMapper.class);
        validator = new LocatarioValidator(aluguelRepository, locatarioRepository);
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
        void deveLancarBusinessExceptionQuandoExistirLocatarioComCPF(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            when(locatarioRepository.existsByCpf(request.cpf())).thenReturn(true);

            assertThrows(BusinessException.class, () -> {
                service.cadastrarLocatario(request);
            });

            verify(locatarioRepository, never()).save(any(Locatario.class));
        }

        @Test
        void deveLancarBusinessExceptionQuandoExistirLocatarioComEmail(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            when(locatarioRepository.existsByCpf(request.cpf())).thenReturn(false);
            when(locatarioRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

            assertThrows(BusinessException.class, () -> {
                service.cadastrarLocatario(request);
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

            when(locatarioRepository.findById(locatario.getId())).thenReturn(Optional.of(locatario));
            when(aluguelRepository.existsByLocatarioIdAndStatus(locatario.getId(), StatusEnum.EM_ANDAMENTO))
                    .thenReturn(false);

            service.deletarLocatario(locatario.getId());

            verify(aluguelRepository, times(1)).deleteByLocatarioId(locatario.getId());
            verify(locatarioRepository, times(1)).deleteById(locatario.getId());
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir(){
            UUID idInvalid = UUID.randomUUID();

            when(locatarioRepository.findById(idInvalid)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                service.deletarLocatario(idInvalid);
            });

            verify(locatarioRepository, never()).save(any(Locatario.class));
        }

        @Test
        void deveLancarConflictBusinessExceptionQuandoLocatarioTiverAluguelEmAndamento(){
            Locatario locatario = LocatarioMock.getLocatarioMock(UUID.randomUUID());

            when(locatarioRepository.findById(locatario.getId())).thenReturn(Optional.of(locatario));
            when(aluguelRepository.existsByLocatarioIdAndStatus(locatario.getId(), StatusEnum.EM_ANDAMENTO))
                    .thenReturn(true);

            assertThrows(ConflictBusinessException.class, () -> {
                service.deletarLocatario(locatario.getId());
            });

            verify(locatarioRepository, never()).save(any(Locatario.class));
        }
    }
}
