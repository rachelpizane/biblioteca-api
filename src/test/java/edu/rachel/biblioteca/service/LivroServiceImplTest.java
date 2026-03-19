package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.enums.StatusLivroEnum;
import edu.rachel.biblioteca.exception.BusinessException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.AutorMapper;
import edu.rachel.biblioteca.mapper.LivroMapper;
import edu.rachel.biblioteca.mock.AutorMock;
import edu.rachel.biblioteca.mock.LivroMock;
import edu.rachel.biblioteca.mock.PageMock;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.model.Livro;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
import edu.rachel.biblioteca.service.impl.LivroServiceImpl;
import edu.rachel.biblioteca.validator.AutorValidator;
import edu.rachel.biblioteca.validator.LivroValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceImplTest {
    @Mock
    private AutorRepository autorRepository;

    @Mock
    private LivroRepository livroRepository;

    private AutorMapper autorMapper;

    private LivroMapper livroMapper;

    private LivroValidator livroValidator;

    private AutorValidator autorValidator;

    private LivroServiceImpl service;

    @BeforeEach
    void setUp() {
        autorMapper = Mappers.getMapper(AutorMapper.class);
        livroMapper = Mappers.getMapper(LivroMapper.class);
        ReflectionTestUtils.setField(livroMapper, "autorMapper", autorMapper);

        livroValidator = new LivroValidator(livroRepository, autorRepository);
        autorValidator = new AutorValidator(autorRepository);

        service = new LivroServiceImpl(autorValidator, livroValidator, livroMapper, autorRepository, livroRepository);
    }

    @Nested
    class CadastrarLivroTests {
        @Test
        void deveSalvarLivroCorretamente(){
            Autor autor = AutorMock.getAutorMock(UUID.randomUUID());
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(List.of(autor.getId()));
            Livro livro = LivroMock.getLivroMock(UUID.randomUUID(), autor.getId());

            when(autorRepository.findAllById(request.autoresIds())).thenReturn(List.of(autor));
            when(livroRepository.save(any(Livro.class))).thenReturn(livro);

            LivroResponseDTO response = service.cadastrarLivro(request);

            assertEquals(livro.getId(), response.id());
            assertThat(response.autores()).hasSize(1);
            assertEquals(autor.getId(), response.autores().getFirst().id());
            verify(livroRepository, times(1)).save(any(Livro.class));
        }

        @Test
        void deveLancarBusinessExceptionQuandoExistirLivroComIsbn(){
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(List.of(UUID.randomUUID()));

            when(livroRepository.existsByIsbn(request.isbn())).thenReturn(true);

            assertThrows(BusinessException.class, () -> {
                service.cadastrarLivro(request);
            });
            verify(livroRepository, never()).save(any(Livro.class));
        }

        @Test
        void deveLancarNotFoundExceptionQuandoAutoresNaoEncontrados(){
            List<UUID> autoresIds = List.of(UUID.randomUUID());
            LivroRequestDTO request = LivroMock.getLivroRequestDTOMock(autoresIds);

            when(autorRepository.findAllById(autoresIds)).thenReturn(List.of());

            assertThrows(NotFoundException.class, () -> {
                service.cadastrarLivro(request);
            });
            verify(livroRepository, never()).save(any(Livro.class));
        }
    }

    @Nested
    class BuscarLivroTests {
        @Test
        void deveBuscarLivroComSucesso() {
            Livro livro = LivroMock.getLivroMock(UUID.randomUUID(), UUID.randomUUID());

            when(livroRepository.findById(livro.getId())).thenReturn(Optional.of(livro));

            LivroResponseDTO response = service.buscarLivro(livro.getId());

            assertEquals(response.id(), livro.getId());
            assertThat(response.autores()).hasSize(1);
        }

        @Test
        void deveLancarNotFoundExceptionQuandoLivroNaoExistir(){
            UUID idInvalid = UUID.randomUUID();

            when(livroRepository.findById(idInvalid)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> {
                service.buscarLivro(idInvalid);
            });
        }
    }

    @Nested
    class BuscarLivrosTests {

        @Test
        void deveBuscarLivrosSemFiltro(){
            List<Livro> livros = List.of(LivroMock.getLivroMock(UUID.randomUUID(), UUID.randomUUID()));
            Pageable pageable = PageMock.getPageableMock();
            Page<Livro> page = PageMock.getPageMock(livros);

            when(livroRepository.findAll(pageable)).thenReturn(page);

            PageResponseDTO<LivroResumoDTO> response = service.buscarLivros(null, pageable);

            List<UUID> idsRetornados = response.conteudo().stream().map(LivroResumoDTO::id).toList();
            List<UUID> idsEsperados = livros.stream().map(Livro::getId).toList();

            assertThat(idsRetornados).hasSize(livros.size());
            assertTrue(idsRetornados.containsAll(idsEsperados));

            assertEquals(page.getNumber(), response.pagina());
            assertEquals(page.getSize(), response.tamanho());
            assertEquals(page.getTotalElements(), response.totalElementos());
            assertEquals(page.getTotalPages(), response.totalPaginas());

            verify(livroRepository, times(1)).findAll(pageable);
            verify(livroRepository, never()).findLivrosDisponiveis(pageable);
            verify(livroRepository, never()).findLivrosAlugados(pageable);
        }

        @ParameterizedTest
        @MethodSource("statusLivrosProvider")
        void deveBuscarLivrosComFiltro(StatusLivroEnum statusLivro, int qntInvocacaoDisponivel, int qntInvocacaoAlugado){
            List<Livro> livros = List.of(LivroMock.getLivroMock(UUID.randomUUID(), UUID.randomUUID()));
            Pageable pageable = PageMock.getPageableMock();
            Page<Livro> page = PageMock.getPageMock(livros);

            Page<Livro> pageMock = switch (statusLivro) {
                case DISPONIVEL -> livroRepository.findLivrosDisponiveis(pageable);
                case ALUGADO -> livroRepository.findLivrosAlugados(pageable);
            };

            when(pageMock).thenReturn(page);

            PageResponseDTO<LivroResumoDTO> response = service.buscarLivros(statusLivro, pageable);

            List<UUID> idsRetornados = response.conteudo().stream().map(LivroResumoDTO::id).toList();
            List<UUID> idsEsperados = livros.stream().map(Livro::getId).toList();

            assertThat(idsRetornados).hasSize(livros.size());
            assertTrue(idsRetornados.containsAll(idsEsperados));

            assertEquals(page.getNumber(), response.pagina());
            assertEquals(page.getSize(), response.tamanho());
            assertEquals(page.getTotalElements(), response.totalElementos());
            assertEquals(page.getTotalPages(), response.totalPaginas());

            verify(livroRepository, never()).findAll(pageable);
            verify(livroRepository, times(qntInvocacaoDisponivel)).findLivrosDisponiveis(pageable);
            verify(livroRepository, times(qntInvocacaoAlugado)).findLivrosAlugados(pageable);
        }

        static Stream<Arguments> statusLivrosProvider() {
            return Stream.of(
                    Arguments.of(StatusLivroEnum.DISPONIVEL, 1, 0),
                    Arguments.of(StatusLivroEnum.ALUGADO, 0, 1)
            );
        }
    }

    @Nested
    class BuscarLivrosAutorTests {
        @Test
        void deveBuscarLivrosPorAutorCorretamente(){
            UUID autorId = UUID.randomUUID();
            Livro livro = LivroMock.getLivroMock(UUID.randomUUID(), autorId);

            when(autorRepository.existsById(autorId)).thenReturn(true);
            when(livroRepository.findLivrosPorAutorId(autorId)).thenReturn(List.of(livro));

            List<LivroResumoDTO> livros = service.buscarLivrosPorAutor(autorId);

            assertThat(livros).hasSize(1);
            assertEquals(livros.getFirst().id(), livro.getId());
        }

        @Test
        void deveLançarNotFoundExceptionQuandoAutorNaoEncontrado(){
            UUID idInvalid = UUID.randomUUID();

            when(autorRepository.existsById(idInvalid)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> {
                service.buscarLivrosPorAutor(idInvalid);
            });
        }
    }
}
