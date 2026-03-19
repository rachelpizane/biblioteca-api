package edu.rachel.biblioteca.service.impl;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.dto.LivroResponseDTO;
import edu.rachel.biblioteca.dto.LivroResumoDTO;
import edu.rachel.biblioteca.dto.PageResponseDTO;
import edu.rachel.biblioteca.enums.StatusLivroEnum;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.LivroMapper;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.model.Livro;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
import edu.rachel.biblioteca.service.LivroService;
import edu.rachel.biblioteca.utils.PageUtils;
import edu.rachel.biblioteca.validator.AutorValidator;
import edu.rachel.biblioteca.validator.LivroValidator;
import edu.rachel.biblioteca.validator.LocatarioValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class LivroServiceImpl implements LivroService {

    private final LocatarioValidator locatarioValidator;
    private final AutorValidator autorValidator;
    private final LivroValidator livroValidator;
    private final LivroMapper mapper;
    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;

    @Override
    public LivroResponseDTO cadastrarLivro(LivroRequestDTO request) {
        livroValidator.validar((request));

        Livro livro = criarLivro(request);
        Livro livroSalvo = livroRepository.save(livro);

        return mapper.paraDto(livroSalvo);
    }

    @Override
    public LivroResponseDTO buscarLivro(UUID id) {
        return livroRepository
                .findById(id)
                .map(mapper::paraDto)
                .orElseThrow(() -> new NotFoundException("Livro não encontrado"));
    }

    @Override
    public PageResponseDTO<LivroResumoDTO> buscarLivros(StatusLivroEnum statusLivro, Pageable pageable) {
        Page<LivroResumoDTO> livros = filtrarLivros(statusLivro, pageable).map(mapper::paraResumoDto);

        return PageUtils.paraPage(livros);
    }

    @Override
    public List<LivroResumoDTO> buscarLivrosPorAutor(UUID autorId) {
        autorValidator.validarExistencia(autorId);
        List<Livro> livros = livroRepository.findLivrosPorAutorId(autorId);

        return mapper.toLivroResumoDTOList(livros);
    }

    @Override
    public List<LivroResumoDTO> buscarLivrosPorLocatario(UUID locatarioId) {
        locatarioValidator.validarExistencia(locatarioId);
        List<Livro> livros = livroRepository.findLivrosAlugadosPorLocatarioId(locatarioId);

        return mapper.toLivroResumoDTOList(livros);
    }

    private Page<Livro> filtrarLivros(StatusLivroEnum statusLivro, Pageable pageable) {
        if (Objects.isNull(statusLivro)) {
            return livroRepository.findAll(pageable);
        }

        return switch (statusLivro) {
            case DISPONIVEL -> livroRepository.findLivrosDisponiveis(pageable);
            case ALUGADO -> livroRepository.findLivrosAlugados(pageable);
        };
    }

    private Livro criarLivro(LivroRequestDTO request) {
        Livro livro = mapper.paraEntidade(request);

        Set<Autor> autores = autorRepository.findAllById(request.autoresIds())
                .stream()
                .collect(Collectors.toSet());

        livro.setAutores(autores);

        return livro;
    }
}
