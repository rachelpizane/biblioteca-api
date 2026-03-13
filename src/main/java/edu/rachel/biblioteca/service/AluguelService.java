package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;


public interface AluguelService {
    AluguelResponseDTO cadastrarAluguel(AluguelRequestDTO request);
}
