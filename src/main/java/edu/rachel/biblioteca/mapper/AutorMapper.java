package edu.rachel.biblioteca.mapper;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.dto.AutorResumoDTO;
import edu.rachel.biblioteca.model.Autor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutorMapper {
    @Mapping(target = "id", ignore = true)
    Autor paraEntidade(AutorRequestDTO request);

    AutorResponseDTO paraDto(Autor autor);

    AutorResumoDTO paraResumoDto(Autor autor);
}
