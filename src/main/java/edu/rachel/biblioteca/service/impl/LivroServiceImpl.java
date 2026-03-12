package edu.rachel.biblioteca.service.impl;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.dto.LivroResponseDTO;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.LivroMapper;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.model.Livro;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
import edu.rachel.biblioteca.service.LivroService;
import edu.rachel.biblioteca.validator.LivroValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class LivroServiceImpl implements LivroService {
    LivroValidator validator;
    LivroMapper mapper;
    AutorRepository autorRepository;
    LivroRepository livroRepository;

    @Override
    public LivroResponseDTO cadastrarLivro(LivroRequestDTO request) {
        validator.validar((request));

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

    private Livro criarLivro(LivroRequestDTO request) {
        Livro livro = mapper.paraEntidade(request);

        Set<Autor> autores = autorRepository.findAllById(request.autoresIds())
                .stream()
                .collect(Collectors.toSet());

        livro.setAutores(autores);

        return livro;
    }
}
