package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.dto.LivroResponseDTO;

import java.util.UUID;

public interface LivroService {
    LivroResponseDTO cadastrarLivro(LivroRequestDTO request);
    LivroResponseDTO buscarLivro(UUID id);
}
