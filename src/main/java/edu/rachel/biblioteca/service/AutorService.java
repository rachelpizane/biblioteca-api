package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;

public interface AutorService {
    AutorResponseDTO criarAutor(AutorRequestDTO request);
}
