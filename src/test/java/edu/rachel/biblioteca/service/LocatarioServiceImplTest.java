package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.LocatarioMapper;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.model.Locatario;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class LocatarioServiceImplTest {
    @Mock
    LocatarioRepository repository;

    LocatarioMapper mapper;

    LocatarioValidator validator;

    LocatarioServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper =  Mappers.getMapper(LocatarioMapper.class);
        validator = new LocatarioValidator(repository);
        service = new LocatarioServiceImpl(validator, mapper, repository);
    }

    @Nested
    class CadastrarLocatarioTests {
        @Test
        void deveSalvarLocatarioCorretamente(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();
            Locatario locatario = LocatarioMock.getLocatarioMock(UUID.randomUUID());

            when(repository.save(any(Locatario.class))).thenReturn(locatario);

            LocatarioResponseDTO response = service.cadastrarLocatario(request);

            assertEquals(response.id(), locatario.getId());
            verify(repository, times(1)).save(any(Locatario.class));
        }

        @Test
        void deveLancarBusinessExceptionQuandoExistirLocatarioComCPF(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            when(repository.existsByCpf(request.cpf())).thenReturn(true);

            assertThrows(BusinessException.class, () -> {
                service.cadastrarLocatario(request);
            });

            verify(repository, never()).save(any(Locatario.class));
        }

        @Test
        void deveLancarBusinessExceptionQuandoExistirLocatarioComEmail(){
            LocatarioRequestDTO request = LocatarioMock.getLocatarioRequestDTOMock();

            when(repository.existsByCpf(request.cpf())).thenReturn(false);
            when(repository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

            assertThrows(BusinessException.class, () -> {
                service.cadastrarLocatario(request);
            });

            verify(repository, never()).save(any(Locatario.class));
        }
    }

    @Nested
    class BuscarLocatarioTests {
        @Test
        void deveBuscarLocatarioComSucesso() {
            Locatario locatario = LocatarioMock.getLocatarioMock(UUID.randomUUID());
            when(repository.findById(locatario.getId())).thenReturn(Optional.of(locatario));

            LocatarioResponseDTO response = service.buscarLocatario(locatario.getId());

            assertEquals(locatario.getId(), response.id());
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir(){
            UUID idInvalid = UUID.randomUUID();

            when(repository.findById(idInvalid)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                service.buscarLocatario(idInvalid);
            });
        }
    }
}
