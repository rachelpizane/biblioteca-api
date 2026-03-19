package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.dto.LivroResponseDTO;
import edu.rachel.biblioteca.dto.LivroResumoDTO;
import edu.rachel.biblioteca.dto.PageResponseDTO;
import edu.rachel.biblioteca.enums.StatusLivroEnum;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LivroService {
    LivroResponseDTO cadastrarLivro(LivroRequestDTO request);

    LivroResponseDTO buscarLivro(UUID id);

    PageResponseDTO<LivroResumoDTO> buscarLivros(StatusLivroEnum statusLivro, Pageable pageable);

    List<LivroResumoDTO> buscarLivrosPorAutor(UUID autorId);

    List<LivroResumoDTO> buscarLivrosPorLocatario(UUID autorId);
}
