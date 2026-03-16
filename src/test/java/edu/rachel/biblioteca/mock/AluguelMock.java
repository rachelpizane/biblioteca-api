package edu.rachel.biblioteca.mock;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.model.Aluguel;
import edu.rachel.biblioteca.model.Locatario;
import edu.rachel.biblioteca.model.Livro;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AluguelMock {

    public static Aluguel getAluguelMock(UUID idAluguel, UUID idLocatario, List<UUID> livrosIds) {
        Aluguel aluguel = getAluguelMock(idLocatario, livrosIds);
        aluguel.setId(idAluguel);

        return aluguel;
    }

    public static Aluguel getAluguelMock(UUID idLocatario, List<UUID> livrosIds) {
        Locatario locatario = LocatarioMock.getLocatarioMock(idLocatario);
        Set<Livro> livros = livrosIds.stream()
                .map(id -> LivroMock.getLivroMock(id, UUID.randomUUID()))
                .collect(Collectors.toSet());

        Aluguel aluguel = new Aluguel();
        aluguel.setDataRetirada(LocalDate.now());
        aluguel.setDataDevolucao(LocalDate.now().plusDays(2));
        aluguel.setStatus(StatusEnum.EM_ANDAMENTO);
        aluguel.setLocatario(locatario);
        aluguel.setLivros(livros);

        return aluguel;
    }

    public static AluguelRequestDTO getAluguelRequestDTOMock() {
        return getAluguelRequestDTOMock(UUID.randomUUID(), List.of(UUID.randomUUID()));
    }

    public static AluguelRequestDTO getAluguelRequestDTOMock(UUID idLocatario, List<UUID> livrosIds) {
        return new AluguelRequestDTO(
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                idLocatario,
                livrosIds
        );
    }

    public static AluguelResponseDTO getAluguelResponseDTOMock() {
        UUID idAluguel = UUID.randomUUID();
        UUID idLocatario = UUID.randomUUID();
        List<UUID> livrosIds = List.of(UUID.randomUUID());

        Aluguel aluguel = getAluguelMock(idAluguel, idLocatario, livrosIds);

        LocatarioResumoDTO locatarioResumo = new LocatarioResumoDTO(
                aluguel.getLocatario().getId(),
                aluguel.getLocatario().getNome()
        );

        List<LivroResumoDTO> livrosResumo = aluguel.getLivros().stream()
                .map(livro -> new LivroResumoDTO(livro.getId(), livro.getNome()))
                .toList();

        return new AluguelResponseDTO(
                aluguel.getId(),
                aluguel.getDataRetirada(),
                aluguel.getDataDevolucao(),
                aluguel.getStatus(),
                locatarioResumo,
                livrosResumo
        );
    }


    public static AluguelRequestDTO getRequestComDataRetiradaEDevolucao(LocalDate dataRetirada, LocalDate dataDevolucao) {
        var request = getAluguelRequestDTOMock();
        return new AluguelRequestDTO(
                dataRetirada,
                dataDevolucao,
                request.locatarioId(),
                request.livrosIds()
        );
    }

    public static AluguelRequestDTO getRequestComLocatarioId(UUID locatarioId) {
        var request = getAluguelRequestDTOMock();
        return new AluguelRequestDTO(
                request.dataRetirada(),
                request.dataDevolucao(),
                locatarioId,
                request.livrosIds()
        );
    }

    public static AluguelRequestDTO getRequestComLivrosIds(List<UUID> livrosIds) {
        var request = getAluguelRequestDTOMock();
        return new AluguelRequestDTO(
                request.dataRetirada(),
                request.dataDevolucao(),
                request.locatarioId(),
                livrosIds
        );
    }

    public static AluguelRequestDTO getRequestComDataDevolucaoEIds(LocalDate dataDevolucao, UUID idLocatario, List<UUID> livrosIds) {
        var request = getAluguelRequestDTOMock(idLocatario, livrosIds);
        return new AluguelRequestDTO(
                request.dataRetirada(),
                dataDevolucao,
                request.locatarioId(),
                request.livrosIds()
        );
    }
}

