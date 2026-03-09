package edu.rachel.biblioteca.model;

import edu.rachel.biblioteca.enums.StatusEnum;
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
@Table(name = "tb_aluguel")
public class Aluguel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private UUID id;

    @Column(nullable = false)
    private LocalDate dataRetirada;

    @Column(nullable = false)
    private LocalDate dataDevolucao;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_locatario", nullable = false)
    private Locatario locatario;

    @ManyToMany
    @JoinTable(
            name="tb_aluguel_livro",
            joinColumns = @JoinColumn(name = "id_aluguel"),
            inverseJoinColumns = @JoinColumn(name = "id_livro")
    )
    private Set<Livro> livros = new HashSet<>();
}
