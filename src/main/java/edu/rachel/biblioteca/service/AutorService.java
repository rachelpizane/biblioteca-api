package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.dto.AutorResumoDTO;
import edu.rachel.biblioteca.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AutorService {
    AutorResponseDTO cadastrarAutor(AutorRequestDTO request);

    AutorResponseDTO buscarAutor(UUID id);

    PageResponseDTO<AutorResumoDTO> buscarAutores(String nome, Pageable pageable);
}
