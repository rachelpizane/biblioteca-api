package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;

import java.util.UUID;

public interface AutorService {
    AutorResponseDTO cadastrarAutor(AutorRequestDTO request);
    AutorResponseDTO buscarAutor(UUID id);
}
