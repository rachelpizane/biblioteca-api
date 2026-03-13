package edu.rachel.biblioteca.service;

import edu.rachel.biblioteca.dto.*;

import java.util.UUID;

public interface LocatarioService {
    LocatarioResponseDTO cadastrarLocatario(LocatarioRequestDTO request);

    LocatarioResponseDTO buscarLocatario(UUID id);
}
