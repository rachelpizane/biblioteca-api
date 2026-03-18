package edu.rachel.biblioteca.mock;

import edu.rachel.biblioteca.dto.PageResponseDTO;
import org.springframework.data.domain.*;

import java.util.List;

public class PageMock {
    public static Pageable getPageableMock(){
        return PageRequest.of(
                0,
                5,
                Sort.by("nome").ascending()
        );
    }

    public static <T> PageResponseDTO getPageResponseDTOMock(List<T> conteudo){
        Pageable pageable = getPageableMock();

        return new PageResponseDTO(
                conteudo,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                conteudo.size(),
                1
        );
    }

    public static <T> Page<T> getPageMock(List<T> conteudo) {
        return new PageImpl<>(conteudo, getPageableMock(), conteudo.size());
    }
}
