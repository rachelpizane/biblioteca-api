package edu.rachel.biblioteca.validator;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.LivroAlugadoException;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.repository.LivroRepository;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Component
public class AluguelValidator {
    LocatarioRepository locatarioRepository;
    LivroRepository livroRepository;

    public void validar(AluguelRequestDTO request) {
        validarExistenciaLocatario(request.locatarioId());
        validarExistenciaLivros(request.livrosIds());
        validarLivrosComAluguelEmAndamento(request.livrosIds());
    }

    private void validarExistenciaLocatario(UUID locatarioId){
        if(!locatarioRepository.existsById(locatarioId)){
            throw new NotFoundException("Locatário não encontrado");
        }
    }

    private void validarExistenciaLivros(List<UUID> livrosIds) {
        List<UUID> livrosIdsRecebidos = new ArrayList<>(livrosIds);
        List<UUID> livrosIdsEncontrados = livroRepository.findLivrosIdsByIdIn(livrosIds);
        
        livrosIdsRecebidos.removeAll(livrosIdsEncontrados );

        if (!livrosIdsRecebidos.isEmpty()) {
            throw new NotFoundException("Livros não encontrados: " + livrosIdsRecebidos);
        }
    }

    private void validarLivrosComAluguelEmAndamento(List<UUID> livrosIds) {
        List<UUID> livrosIdsAlugados = livroRepository
                .findLivrosIdsComAluguelEmAndamento(livrosIds, StatusEnum.EM_ANDAMENTO);

        if (!livrosIdsAlugados.isEmpty()) {
            throw new LivroAlugadoException("Livros com aluguel em andamento: " + livrosIdsAlugados);
        }
    }
}
