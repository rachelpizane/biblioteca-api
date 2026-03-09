package edu.rachel.biblioteca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_livro")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    LocalDate dataPublicacao;

    @ManyToMany
    @JoinTable(
            name = "tb_autor_livro",
            joinColumns = @JoinColumn(name = "id_livro"),
            inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    private Set<Autor> autores = new HashSet<>();
}
