package web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import model.Area;
import model.Candidatura;
import model.Modalidade;
import model.Perfil;
import model.Usuario;
import model.Vaga;

import java.time.LocalDateTime;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record StudentRegistration(
            @NotBlank @Size(max = 120) String nome,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 8, max = 72) String senha,
            @NotBlank @Size(max = 120) String curso) {
    }

    public record CompanyRegistration(
            @NotBlank @Size(max = 120) String nome,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 8, max = 72) String senha,
            @NotBlank @Size(max = 150) String nomeEmpresa) {
    }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(max = 72) String senha) {
    }

    public record UserResponse(
            Long id, String nome, String email, Perfil perfil,
            String curso, String nomeEmpresa) {
        public static UserResponse from(Usuario usuario) {
            return new UserResponse(usuario.getId(), usuario.getNome(),
                    usuario.getEmail(), usuario.getPerfil(),
                    usuario.getCurso(), usuario.getNomeEmpresa());
        }
    }

    public record AreaRequest(@NotBlank @Size(max = 100) String nome) {
    }

    public record AreaResponse(Long id, String nome) {
        public static AreaResponse from(Area area) {
            return new AreaResponse(area.getId(), area.getNome());
        }
    }

    public record VacancyRequest(
            @NotBlank @Size(max = 150) String titulo,
            @NotBlank String descricao,
            @NotNull Long areaId,
            @NotBlank @Size(max = 150) String localizacao,
            @NotNull Modalidade modalidade,
            @NotNull @Positive Integer cargaHorariaSemanal) {
    }

    public record VacancyResponse(
            Long id, String titulo, String descricao, AreaResponse area,
            String localizacao, Modalidade modalidade, int cargaHorariaSemanal,
            String situacao, Long empresaId, String nomeEmpresa) {
        public static VacancyResponse from(Vaga vaga) {
            Usuario empresa = vaga.getEmpresaResponsavel();
            return new VacancyResponse(vaga.getId(), vaga.getTitulo(),
                    vaga.getDescricao(), AreaResponse.from(vaga.getArea()),
                    vaga.getLocalizacao(), vaga.getModalidade(),
                    vaga.getCargaHorariaSemanal(), vaga.getSituacao().name(),
                    empresa.getId(), empresa.getNomeEmpresa());
        }
    }

    public record ApplicantResponse(
            Long id, UserResponse estudante, VacancySummary vaga,
            LocalDateTime dataInscricao) {
        public static ApplicantResponse from(Candidatura candidatura) {
            return new ApplicantResponse(candidatura.getId(),
                    UserResponse.from(candidatura.getEstudante()),
                    VacancySummary.from(candidatura.getVaga()),
                    candidatura.getDataInscricao());
        }
    }

    public record VacancySummary(Long id, String titulo) {
        public static VacancySummary from(Vaga vaga) {
            return new VacancySummary(vaga.getId(), vaga.getTitulo());
        }
    }

    public record ErrorResponse(
            LocalDateTime timestamp, int status, String code,
            String message, String path) {
    }
}
