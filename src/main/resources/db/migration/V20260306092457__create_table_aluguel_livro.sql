CREATE TABLE IF NOT EXISTS tb_aluguel_livro (
      id_aluguel UUID,
      id_livro UUID,
      PRIMARY KEY (id_aluguel, id_livro),
      FOREIGN KEY (id_aluguel) REFERENCES tb_aluguel (id) ON DELETE CASCADE,
      FOREIGN KEY (id_livro) REFERENCES tb_livro (id) ON DELETE RESTRICT
);