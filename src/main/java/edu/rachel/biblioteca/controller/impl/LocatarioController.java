package edu.rachel.biblioteca.controller.impl;

import edu.rachel.biblioteca.controller.LocatarioApi;
import edu.rachel.biblioteca.dto.LivroResumoDTO;
import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.service.LivroService;
import edu.rachel.biblioteca.service.LocatarioService;
import edu.rachel.biblioteca.utils.UriUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class LocatarioController implements LocatarioApi {

    private final LocatarioService locatarioService;
    private final LivroService livroService;

    @Override
    public ResponseEntity<LocatarioResponseDTO> cadastrarLocatario(LocatarioRequestDTO request) {
        LocatarioResponseDTO response = locatarioService.cadastrarLocatario(request);
        URI location = UriUtils.construirLocation(response.id());

        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<LocatarioResponseDTO> atualizarLocatario(UUID id, LocatarioRequestDTO request) {
        LocatarioResponseDTO response = locatarioService.atualizarLocatario(id, request);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LocatarioResponseDTO> buscarLocatario(UUID id) {
        LocatarioResponseDTO response = locatarioService.buscarLocatario(id);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deletarLocatario(UUID id) {
        locatarioService.deletarLocatario(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<LivroResumoDTO>> buscarLivrosPorLocatario(UUID id) {
        List<LivroResumoDTO> response = livroService.buscarLivrosPorLocatario(id);

        return ResponseEntity.ok(response);
    }
}