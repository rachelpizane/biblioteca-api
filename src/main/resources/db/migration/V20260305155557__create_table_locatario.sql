CREATE TABLE IF NOT EXISTS tb_locatario (
      id UUID PRIMARY KEY,
      cpf VARCHAR(11) NOT NULL UNIQUE,
      nome VARCHAR(200) NOT NULL,
      sexo VARCHAR(20) CHECK (sexo IN ('MASCULINO', 'FEMININO', 'OUTRO')),
      data_nascimento DATE NOT NULL CHECK (data_nascimento < CURRENT_DATE),
      email VARCHAR(255) NOT NULL UNIQUE,
      telefone VARCHAR(15) NOT NULL
);