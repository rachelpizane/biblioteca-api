CREATE TABLE IF NOT EXISTS tb_autor_livro (
      id_autor UUID,
      id_livro UUID,
      PRIMARY KEY (id_autor, id_livro),
      FOREIGN KEY (id_autor) REFERENCES tb_autor (id) ON DELETE RESTRICT,
      FOREIGN KEY (id_livro) REFERENCES tb_livro (id) ON DELETE CASCADE
);