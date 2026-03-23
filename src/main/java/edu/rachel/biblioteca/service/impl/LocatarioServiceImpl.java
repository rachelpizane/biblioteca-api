package edu.rachel.biblioteca.service.impl;

import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.LocatarioMapper;
import edu.rachel.biblioteca.model.Locatario;
import edu.rachel.biblioteca.repository.AluguelRepository;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import edu.rachel.biblioteca.service.LocatarioService;
import edu.rachel.biblioteca.validator.LocatarioValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@Service
public class LocatarioServiceImpl implements LocatarioService {
    private final LocatarioValidator validator;
    private final LocatarioMapper mapper;
    private final LocatarioRepository locatarioRepository;
    private final AluguelRepository aluguelRepository;

    @Override
    public LocatarioResponseDTO cadastrarLocatario(LocatarioRequestDTO request) {
        validator.validar(request);
        Locatario locatarioSalvo = locatarioRepository.save(mapper.paraEntidade(request));

        return mapper.paraDto(locatarioSalvo);
    }

    @Override
    public LocatarioResponseDTO atualizarLocatario(UUID id, LocatarioRequestDTO request) {
        validator.validarAtualizacao(id, request);
        Locatario locatarioAtualizado = locatarioRepository.save(mapper.paraEntidade(id, request));

        return mapper.paraDto(locatarioAtualizado);
    }

    @Override
    public LocatarioResponseDTO buscarLocatario(UUID id) {
        return mapper.paraDto(buscarLocatarioById(id));
    }

    @Override
    @Transactional
    public void deletarLocatario(UUID id) {
        validator.validarExclusao(id);

        aluguelRepository.deleteByLocatarioId(id);
        locatarioRepository.deleteById(id);
    }

    private Locatario buscarLocatarioById(UUID id) {
        return locatarioRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Locatário não encontrado"));
    }
}
