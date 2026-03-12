package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.dto.LivroResponseDTO;

public interface LivroService {
    LivroResponseDTO cadastrarLivro(LivroRequestDTO request);
}
