package edu.rachel.biblioteca.mapper;

import edu.rachel.biblioteca.dto.*;
import edu.rachel.biblioteca.model.Locatario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocatarioMapper {
    @Mapping(target = "id", ignore = true)
    Locatario paraEntidade(LocatarioRequestDTO request);

    LocatarioResponseDTO paraDto(Locatario locatario);

    LocatarioResumoDTO paraResumoDto(Locatario locatario);
}