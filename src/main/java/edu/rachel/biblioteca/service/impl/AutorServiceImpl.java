package edu.rachel.biblioteca.service.impl;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.mapper.AutorMapper;
import edu.rachel.biblioteca.model.Autor;
import edu.rachel.biblioteca.repository.AutorRepository;
import edu.rachel.biblioteca.service.AutorService;
import edu.rachel.biblioteca.validator.AutorValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AutorServiceImpl implements AutorService {
    AutorValidator validator;
    AutorMapper mapper;
    AutorRepository repository;

    @Override
    public AutorResponseDTO criarAutor(AutorRequestDTO request) {
        validator.validar(request);
        Autor autorSalvo = repository.save(mapper.paraEntidade(request));

        return mapper.paraDto(autorSalvo);
    }
}
