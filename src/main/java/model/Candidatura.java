
package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidaturas", uniqueConstraints = {
        @UniqueConstraint(name = "candidatura_unica", columnNames = {"estudante_id", "vaga_id"})
})
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estudante_id", nullable = false)
    private Usuario estudante;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @Column(name = "data_inscricao", nullable = false)
    private LocalDateTime dataInscricao;

    protected Candidatura() {
    }

    public Candidatura(Long id, Usuario estudante,
                       Vaga vaga, LocalDateTime dataInscricao) {

        this.id = id;
        this.estudante = estudante;
        this.vaga = vaga;
        this.dataInscricao = dataInscricao;
    }

    public Long getId() {
        return id;
    }

    public Usuario getEstudante() {
        return estudante;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    @Override
    public String toString() {
        return "Candidatura{" +
                "id=" + id +
                ", estudante=" + estudante.getNome() +
                ", vaga=" + vaga.getTitulo() +
                ", dataInscricao=" + dataInscricao +
                '}';
    }
}
