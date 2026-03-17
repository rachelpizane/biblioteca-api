UPDATE tb_aluguel SET status = 'FINALIZADO' WHERE status = 'CONCLUIDO';

DO $$
BEGIN
      IF EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name = 'tb_aluguel_status_check'
                  AND table_name = 'tb_aluguel'
      ) THEN
            EXECUTE 'ALTER TABLE tb_aluguel DROP CONSTRAINT tb_aluguel_status_check';
      END IF;

      IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name = 'tb_aluguel_status_check'
                  AND table_name = 'tb_aluguel'
      ) THEN
            EXECUTE 'ALTER TABLE tb_aluguel ADD CONSTRAINT tb_aluguel_status_check CHECK (status IN (''FINALIZADO'', ''EM_ANDAMENTO'', ''CANCELADO''))';
      END IF;
END$$;