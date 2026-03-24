package edu.rachel.biblioteca.service.impl;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.dto.AutorResumoDTO;
import edu.rachel.biblioteca.dto.PageResponseDTO;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.AutorMapper;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.service.AutorService;
import edu.rachel.biblioteca.utils.PageUtils;
import edu.rachel.biblioteca.validator.AutorValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AutorServiceImpl implements AutorService {

    private final AutorValidator validator;
    private final AutorMapper mapper;
    private final AutorRepository repository;

    @Override
    public AutorResponseDTO cadastrarAutor(AutorRequestDTO request) {
        validator.validarCadastro(request);
        Autor autorSalvo = repository.save(mapper.paraEntidade(request));

        return mapper.paraDto(autorSalvo);
    }

    @Override
    public AutorResponseDTO buscarAutor(UUID id) {
        return repository
                .findById(id)
                .map(mapper::paraDto)
                .orElseThrow(() -> new NotFoundException("Autor não encontrado"));
    }

    @Override
    public PageResponseDTO<AutorResumoDTO> buscarAutores(String nome, Pageable pageable) {
        Page<AutorResumoDTO> page = filtrarAutores(nome, pageable)
                .map(mapper::paraResumoDto);

        return PageUtils.paraPage(page);
    }

    private Page<Autor> filtrarAutores(String nome, Pageable pageable) {
        return Objects.isNull(nome) || nome.isBlank() ?
                repository.findAll(pageable) :
                repository.findByNomeContainingIgnoreCase(nome, pageable);
    }
}
