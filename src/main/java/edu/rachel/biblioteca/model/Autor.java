package edu.rachel.biblioteca.model;

import edu.rachel.biblioteca.enums.SexoEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_autor")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Enumerated(value = EnumType.STRING)
    private SexoEnum sexo;

    @Column(nullable = false)
    private int anoNascimento;
}
