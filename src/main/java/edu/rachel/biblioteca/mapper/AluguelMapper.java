package edu.rachel.biblioteca.mapper;

import edu.rachel.biblioteca.dto.AluguelRequestDTO;
import edu.rachel.biblioteca.dto.AluguelResponseDTO;
import edu.rachel.biblioteca.model.Aluguel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(
        componentModel = "spring",
        uses = {LocatarioMapper.class, LivroMapper.class}
)
public interface AluguelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "locatario", ignore = true)
    @Mapping(target = "livros", ignore = true)
    Aluguel paraEntidade(AluguelRequestDTO request);

    AluguelResponseDTO paraDto(Aluguel aluguel);
}