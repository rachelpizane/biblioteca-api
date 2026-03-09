CREATE TABLE IF NOT EXISTS tb_aluguel (
      id UUID PRIMARY KEY,
      data_retirada DATE NOT NULL CHECK (data_retirada <= CURRENT_DATE),
      data_devolucao DATE NOT NULL CHECK (data_devolucao >= data_retirada),
      status VARCHAR(20) CHECK (status in ('CONCLUIDO', 'EM_ANDAMENTO', 'CANCELADO')),
      id_locatario UUID NOT NULL,
      FOREIGN KEY (id_locatario) REFERENCES tb_locatario (id) ON DELETE RESTRICT
);