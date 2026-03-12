package edu.rachel.biblioteca.mapper;

import edu.rachel.biblioteca.dto.LivroRequestDTO;
import edu.rachel.biblioteca.dto.LivroResponseDTO;
import edu.rachel.biblioteca.model.Livro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AutorMapper.class})
public interface LivroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "autores", ignore = true)
    Livro paraEntidade(LivroRequestDTO request);

    LivroResponseDTO paraDto(Livro livro);
}

