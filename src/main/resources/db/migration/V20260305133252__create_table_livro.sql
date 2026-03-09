CREATE TABLE IF NOT EXISTS tb_livro (
      id UUID PRIMARY KEY,
      isbn VARCHAR(13) NOT NULL UNIQUE,
      nome VARCHAR(200) NOT NULL,
      data_publicacao DATE NOT NULL
);