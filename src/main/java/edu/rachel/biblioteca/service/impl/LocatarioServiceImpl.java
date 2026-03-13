package edu.rachel.biblioteca.service.impl;

import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.LocatarioMapper;
import edu.rachel.biblioteca.model.Locatario;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import edu.rachel.biblioteca.service.LocatarioService;
import edu.rachel.biblioteca.validator.LocatarioValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class LocatarioServiceImpl implements LocatarioService {
    LocatarioValidator validator;
    LocatarioMapper mapper;
    LocatarioRepository repository;

    @Override
    public LocatarioResponseDTO cadastrarLocatario(LocatarioRequestDTO request) {
        validator.validar(request);
        Locatario locatarioSalvo = repository.save(mapper.paraEntidade(request));

        return mapper.paraDto(locatarioSalvo);
    }

    @Override
    public LocatarioResponseDTO buscarLocatario(UUID id) {
        return repository
                .findById(id)
                .map(mapper::paraDto)
                .orElseThrow(() -> new NotFoundException("Locatário não encontrado"));
    }
}
