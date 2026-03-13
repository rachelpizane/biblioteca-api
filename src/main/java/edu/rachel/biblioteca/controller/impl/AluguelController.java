package edu.rachel.biblioteca.controller.impl;

import edu.rachel.biblioteca.controller.AluguelApi;
import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;
import edu.rachel.biblioteca.service.AluguelService;
import edu.rachel.biblioteca.utils.UriUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@AllArgsConstructor
@RestController
public class AluguelController implements AluguelApi {
    private final AluguelService service;

    @Override
    public ResponseEntity<AluguelResponseDTO> cadastrarAluguel(AluguelRequestDTO request) {
        AluguelResponseDTO response = service.cadastrarAluguel(request);
        URI location = UriUtils.construirLocation(response.id());

        return ResponseEntity.created(location).body(response);
    }
}
