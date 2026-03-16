package edu.rachel.biblioteca.service.impl;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;
import edu.rachel.biblioteca.enums.StatusEnum;
import edu.rachel.biblioteca.exception.NotFoundException;
import edu.rachel.biblioteca.mapper.AluguelMapper;
import edu.rachel.biblioteca.model.Aluguel;
import edu.rachel.biblioteca.model.Livro;
import edu.rachel.biblioteca.model.Locatario;
import edu.rachel.biblioteca.repository.AluguelRepository;
import edu.rachel.biblioteca.repository.LivroRepository;
import edu.rachel.biblioteca.repository.LocatarioRepository;
import edu.rachel.biblioteca.service.AluguelService;
import edu.rachel.biblioteca.validator.AluguelValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AluguelServiceImpl implements AluguelService {
    private final AluguelValidator validator;
    private final AluguelMapper mapper;
    private final AluguelRepository aluguelRepository;
    private final LivroRepository livroRepository;
    private final LocatarioRepository locatarioRepository;

    @Override
    public AluguelResponseDTO cadastrarAluguel(AluguelRequestDTO request) {
        validator.validar(request);

        Aluguel aluguel = criarAluguel(request);
        Aluguel aluguelSalvo = aluguelRepository.save(aluguel);

        return mapper.paraDto(aluguelSalvo);
    }

    @Override
    public AluguelResponseDTO buscarAluguel(UUID id) {
        return aluguelRepository
                .findById(id)
                .map(mapper::paraDto)
                .orElseThrow(() -> new NotFoundException("Aluguel não encontrado"));
    }

    private Aluguel criarAluguel(AluguelRequestDTO request) {
        Aluguel aluguel = mapper.paraEntidade(request);
        aluguel.setStatus(StatusEnum.EM_ANDAMENTO);

        definirDataDevolucaoSeNula(aluguel, request);
        definirRelacionamentos(aluguel, request);

        return aluguel;
    }

    private void definirDataDevolucaoSeNula(Aluguel aluguel, AluguelRequestDTO request) {
        if (Objects.isNull(request.dataDevolucao())) {
            LocalDate dataDevolucao = request.dataRetirada().plusDays(2);
            aluguel.setDataDevolucao(dataDevolucao);
        }
    }

    private void definirRelacionamentos(Aluguel aluguel, AluguelRequestDTO request){
        Set<Livro> livros = livroRepository.findAllById(request.livrosIds()).stream().collect(Collectors.toSet());
        Locatario locatario = locatarioRepository.findById(request.locatarioId())
                .orElseThrow(() -> new NotFoundException("Locatário não encontrado"));

        aluguel.setLivros(livros);
        aluguel.setLocatario(locatario);
    }
}
