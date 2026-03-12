package edu.rachel.biblioteca.mock;

import edu.rachel.biblioteca.dto.LocatarioRequestDTO;
import edu.rachel.biblioteca.dto.LocatarioResponseDTO;
import edu.rachel.biblioteca.enums.SexoEnum;
import edu.rachel.biblioteca.model.Locatario;

import java.time.LocalDate;
import java.util.UUID;

public class LocatarioMock {

    public static LocatarioResponseDTO getLocatarioResponseDTOMock() {
        Locatario locatario = getLocatarioMock(UUID.randomUUID());

        return new LocatarioResponseDTO(
                locatario.getId(),
                locatario.getCpf(),
                locatario.getNome(),
                locatario.getSexo(),
                locatario.getEmail(),
                locatario.getTelefone(),
                locatario.getDataNascimento()
        );
    }

    public static Locatario getLocatarioMock(UUID id) {
        Locatario locatario = getLocatarioMock();
        locatario.setId(id);

        return locatario;
    }

    public static Locatario getLocatarioMock() {
        LocatarioRequestDTO request = getLocatarioRequestDTOMock();
        Locatario locatario = new Locatario();
        locatario.setCpf(request.cpf());
        locatario.setNome(request.nome());
        locatario.setSexo(request.sexo());
        locatario.setEmail(request.email());
        locatario.setTelefone(request.telefone());
        locatario.setDataNascimento(request.dataNascimento());

        return locatario;
    }

    public static LocatarioRequestDTO getLocatarioRequestDTOMock() {
        return new LocatarioRequestDTO(
                "10987654321",
                "Maria Oliveira",
                SexoEnum.FEMININO,
                "maria.oliveira@email.com",
                "11998765432",
                LocalDate.of(1990, 5, 15)
        );
    }

    public static LocatarioRequestDTO getRequestComCpf(String cpf) {
        var request = getLocatarioRequestDTOMock();
        return new LocatarioRequestDTO(
                cpf,
                request.nome(),
                request.sexo(),
                request.email(),
                request.telefone(),
                request.dataNascimento()
        );
    }

    public static LocatarioRequestDTO getRequestComNome(String nome) {
        var request = getLocatarioRequestDTOMock();
        return new LocatarioRequestDTO(
                request.cpf(),
                nome,
                request.sexo(),
                request.email(),
                request.telefone(),
                request.dataNascimento()
        );
    }

    public static LocatarioRequestDTO getRequestComEmail(String email) {
        var request = getLocatarioRequestDTOMock();
        return new LocatarioRequestDTO(
                request.cpf(),
                request.nome(),
                request.sexo(),
                email,
                request.telefone(),
                request.dataNascimento()
        );
    }

    public static LocatarioRequestDTO getRequestComTelefone(String telefone) {
        var request = getLocatarioRequestDTOMock();
        return new LocatarioRequestDTO(
                request.cpf(),
                request.nome(),
                request.sexo(),
                request.email(),
                telefone,
                request.dataNascimento()
        );
    }

    public static LocatarioRequestDTO getRequestComDataNascimento(LocalDate data) {
        var request = getLocatarioRequestDTOMock();

        return new LocatarioRequestDTO(
                request.cpf(),
                request.nome(),
                request.sexo(),
                request.email(),
                request.telefone(),
                data
        );
    }
}
