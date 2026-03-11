package edu.rachel.biblioteca.mock;

import edu.rachel.biblioteca.dto.AutorRequestDTO;
import edu.rachel.biblioteca.dto.AutorResponseDTO;
import edu.rachel.biblioteca.enums.SexoEnum;
import edu.rachel.biblioteca.model.Autor;

import java.util.UUID;

public class AutorMock {

    public static AutorResponseDTO getAutorResponseDTOMock(){
        Autor autor = getAutorMock(UUID.randomUUID());

        return new AutorResponseDTO(
                autor.getId(),
                autor.getCpf(),
                autor.getNome(),
                autor.getSexo(),
                autor.getAnoNascimento()
        );
    }

    public static Autor getAutorMock(UUID id){
        Autor autor = getAutorMock();
        autor.setId(id);
        return autor;
    }

    public static Autor getAutorMock(){
        AutorRequestDTO request = getAutorRequestDTOMock();
        Autor autor = new Autor();
        autor.setNome(request.nome());
        autor.setCpf(request.cpf());
        autor.setSexo(request.sexo());
        autor.setAnoNascimento(request.anoNascimento());

        return autor;
    }

    public static AutorRequestDTO getAutorRequestDTOMock(){
        return new AutorRequestDTO(
                "48323788723",
                "Carlos Silva",
                SexoEnum.MASCULINO,
                1986
        );
    }
}
