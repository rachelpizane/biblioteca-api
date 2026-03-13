package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.AutorMapper;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.service.impl.AutorServiceImpl;
import edu.rachel.biblioteca.validator.AutorValidator;
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

@ExtendWith(MockitoExtension.class)
class AutorServiceImplTest {
    @Mock
    AutorRepository repository;

    AutorMapper mapper;

    AutorValidator validator;

    AutorServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper =  Mappers.getMapper(AutorMapper.class);
        validator = new AutorValidator(repository);
        service = new AutorServiceImpl(validator, mapper, repository);
    }

    @Nested
    class CadastrarAutorTests {
        @Test
        void deveSalvarAutorCorretamente(){
            AutorRequestDTO request = AutorMock.getAutorRequestDTOMock();
            Autor autor = AutorMock.getAutorMock(UUID.randomUUID());

            when(repository.save(any(Autor.class))).thenReturn(autor);

            AutorResponseDTO response = service.cadastrarAutor(request);

            assertEquals(autor.getId(), response.id());
            verify(repository, times(1)).save(any(Autor.class));
        }

        @Test
        void deveLancarBusinessExceptionQuandoExistirAutorComCPF(){
            AutorRequestDTO request = AutorMock.getAutorRequestDTOMock();

            when(repository.existsByCpf(request.cpf())).thenReturn(true);

            assertThrows(BusinessException.class, () -> {
                service.cadastrarAutor(request);
            });
            verify(repository, never()).save(any(Autor.class));
        }
    }

    @Nested
    class BuscarAutorTests{
        @Test
        void deveBuscarAutorComSucesso() {
            Autor autor = AutorMock.getAutorMock(UUID.randomUUID());
            when(repository.findById(autor.getId())).thenReturn(Optional.of(autor));

            AutorResponseDTO response = service.buscarAutor(autor.getId());

            assertEquals(autor.getId(), response.id());
        }

        @Test
        void deveLancarNotFoundExceptionQuandoAutorNaoExistir(){
            UUID idInvalid = UUID.randomUUID();

            when(repository.findById(idInvalid)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                service.buscarAutor(idInvalid);
            });
        }
    }
}