package edu.rachel.biblioteca.utils;

import edu.rachel.biblioteca.dto.PageResponseDTO;
import org.springframework.data.domain.Page;

public class PageUtils {
    public static <T> PageResponseDTO<T> paraPage(Page<T> page) {
        return new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    private PageUtils(){}
}
