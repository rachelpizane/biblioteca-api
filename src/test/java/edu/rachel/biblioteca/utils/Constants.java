package edu.rachel.biblioteca.utils;

public class Constants {
    public static final String AUTOR_URL = "/autores";
    public static final String AUTOR_ID_URL = AUTOR_URL  + "/{id}";
    public static final String AUTOR_LIVROS_URL = AUTOR_ID_URL + "/livros";

    public static final String LIVRO_URL = "/livros";
    public static final String LIVRO_ID_URL = LIVRO_URL  + "/{id}";

    public static final String LOCATARIO_URL = "/locatarios";
    public static final String LOCATARIO_ID_URL = LOCATARIO_URL  + "/{id}";
    public static final String LOCATARIO_LIVROS_URL = LOCATARIO_ID_URL + "/livros";

    public static final String ALUGUEL_URL = "/alugueis";
    public static final String ALUGUEL_ID_URL = ALUGUEL_URL + "/{id}";
    public static final String ALUGUEL_STATUS_URL = ALUGUEL_ID_URL + "/status";
}
