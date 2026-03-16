package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.LivroAlugadoException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.AluguelMapper;
import edu.rachel.biblioteca.mapper.LivroMapper;
import edu.rachel.biblioteca.mapper.LocatarioMapper;
import edu.rachel.biblioteca.mock.AluguelMock;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.mock.LocatarioMock;
import edu.rachel.biblioteca.model.Aluguel;
import edu.rachel.biblioteca.model.Livro;
import edu.rachel.biblioteca.model.Locatario;
import edu.rachel.biblioteca.repository.AluguelRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import edu.rachel.biblioteca.service.impl.AluguelServiceImpl;
import edu.rachel.biblioteca.validator.AluguelValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AluguelServiceImplTest {
    @Mock
    private LocatarioRepository locatarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private AluguelRepository aluguelRepository;

    private LocatarioMapper locatarioMapper;

    private LivroMapper livroMapper;

    private AluguelMapper aluguelMapper;

    private AluguelValidator validator;

    private AluguelServiceImpl service;

    @BeforeEach
    void setUp() {
        locatarioMapper = Mappers.getMapper(LocatarioMapper.class);
        livroMapper = Mappers.getMapper(LivroMapper.class);
        aluguelMapper = Mappers.getMapper(AluguelMapper.class);

        ReflectionTestUtils.setField(aluguelMapper, "locatarioMapper", locatarioMapper);
        ReflectionTestUtils.setField(aluguelMapper, "livroMapper", livroMapper);

        validator = new AluguelValidator(locatarioRepository, livroRepository);
        service = new AluguelServiceImpl(validator, aluguelMapper, aluguelRepository, livroRepository, locatarioRepository);
    }

    @Nested
    class CadastrarAluguelTests {
        @Test
        void deveSalvarAluguelCorretamente(){
            Locatario locatario = LocatarioMock.getLocatarioMock(UUID.randomUUID());
            Livro livro = LivroMock.getLivroMock(UUID.randomUUID(), UUID.randomUUID());

            UUID locatarioId = locatario.getId();
            List<UUID> livrosId = List.of(livro.getId());

            AluguelRequestDTO request = AluguelMock.getRequestComDataDevolucaoEIds(null, locatarioId, livrosId);
            Aluguel aluguel = AluguelMock.getAluguelMock(locatarioId, livrosId);

            when(locatarioRepository.existsById(locatarioId)).thenReturn(true);
            when(livroRepository.findLivrosIdsByIdIn(livrosId)).thenReturn(livrosId);
            when(livroRepository.findLivrosIdsComAluguelEmAndamento(
                    livrosId, StatusEnum.EM_ANDAMENTO)).thenReturn(List.of());

            when(locatarioRepository.findById(locatarioId)).thenReturn(Optional.of(locatario));
            when(livroRepository.findAllById(livrosId)).thenReturn(List.of(livro));
            when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);

            AluguelResponseDTO response = service.cadastrarAluguel(request);

            assertEquals(aluguel.getId(), response.id());
            assertEquals(StatusEnum.EM_ANDAMENTO, response.status());
            assertEquals(response.dataRetirada().plusDays(2),response.dataDevolucao());
            assertEquals(aluguel.getLocatario().getId(), response.locatario().id());
            assertThat(response.livros()).hasSize(1);
            assertEquals(livro.getId(), response.livros().getFirst().id());
            verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoEncontrado(){
            UUID locatarioId = UUID.randomUUID();
            List<UUID> livrosId = List.of(UUID.randomUUID());
            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(locatarioId, livrosId);

            when(locatarioRepository.existsById(locatarioId)).thenReturn(false);


            assertThrows(NotFoundException.class, () -> {
                service.cadastrarAluguel(request);
            });
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLivrosNaoEncontrados(){
            UUID locatarioId = UUID.randomUUID();
            List<UUID> livrosId = List.of(UUID.randomUUID());
            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(locatarioId, livrosId);

            when(locatarioRepository.existsById(locatarioId)).thenReturn(true);
            when(livroRepository.findLivrosIdsByIdIn(livrosId)).thenReturn(List.of());

            assertThrows(NotFoundException.class, () -> {
                service.cadastrarAluguel(request);
            });
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        void deveLancarLivroAlugadoExceptionQuandoLivrosNaoEncontrados(){
            UUID locatarioId = UUID.randomUUID();
            List<UUID> livrosId = List.of(UUID.randomUUID());
            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(locatarioId, livrosId);

            when(locatarioRepository.existsById(locatarioId)).thenReturn(true);
            when(livroRepository.findLivrosIdsByIdIn(livrosId)).thenReturn(livrosId);
            when(livroRepository.findLivrosIdsComAluguelEmAndamento(
                    livrosId, StatusEnum.EM_ANDAMENTO)).thenReturn(livrosId);

            assertThrows(LivroAlugadoException.class, () -> {
                service.cadastrarAluguel(request);
            });
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoEncontradoAposValidacoesInicias(){
            UUID locatarioId = UUID.randomUUID();
            List<UUID> livrosId = List.of(UUID.randomUUID());
            AluguelRequestDTO request = AluguelMock.getAluguelRequestDTOMock(locatarioId, livrosId);

            when(locatarioRepository.existsById(locatarioId)).thenReturn(true);
            when(livroRepository.findLivrosIdsByIdIn(livrosId)).thenReturn(livrosId);
            when(livroRepository.findLivrosIdsComAluguelEmAndamento(
                    livrosId, StatusEnum.EM_ANDAMENTO)).thenReturn(List.of());

            when(locatarioRepository.findById(locatarioId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                service.cadastrarAluguel(request);
            });
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }
    }

    @Nested
    class BuscarAluguelTests {
        @Test
        void deveBuscarAluguelComSucesso() {
            Aluguel aluguel = AluguelMock.getAluguelMock(UUID.randomUUID(), List.of(UUID.randomUUID()));

            when(aluguelRepository.findById(aluguel.getId())).thenReturn(Optional.of(aluguel));

            AluguelResponseDTO response = service.buscarAluguel(aluguel.getId());

            assertEquals(aluguel.getId(), response.id());
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLocatarioNaoExistir(){
            UUID idInvalid = UUID.randomUUID();

            when(aluguelRepository.findById(idInvalid)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                service.buscarAluguel(idInvalid);
            });
        }
    }
}
