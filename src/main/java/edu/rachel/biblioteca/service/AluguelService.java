package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;
import edu.rachel.biblioteca.dto.StatusResponseDTO;
import edu.rachel.biblioteca.enums.StatusEnum;

import java.util.UUID;


public interface AluguelService {
    AluguelResponseDTO cadastrarAluguel(AluguelRequestDTO request);

    AluguelResponseDTO buscarAluguel(UUID id);

    StatusResponseDTO atualizarStatusAluguel(UUID id, StatusEnum status);
}
