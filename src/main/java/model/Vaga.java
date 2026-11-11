//ABCDEFGHIJ
package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vagas")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Usuario empresaResponsavel;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String descricao;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(nullable = false, length = 150)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Modalidade modalidade;

    @Column(name = "carga_horaria_semanal", nullable = false)
    private int cargaHorariaSemanal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoVaga situacao;

    protected Vaga() {
    }

    public Vaga(Long id, Usuario empresaResponsavel,
                String titulo, String descricao, Area area,
                String localizacao, Modalidade modalidade,
                int cargaHorariaSemanal, SituacaoVaga situacao) {

        this.id = id;
        this.empresaResponsavel = empresaResponsavel;
        this.titulo = titulo;
        this.descricao = descricao;
        this.area = area;
        this.localizacao = localizacao;
        this.modalidade = modalidade;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public Usuario getEmpresaResponsavel() {
        return empresaResponsavel;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Area getArea() {
        return area;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public int getCargaHorariaSemanal() {
        return cargaHorariaSemanal;
    }

    public SituacaoVaga getSituacao() {
        return situacao;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public void setModalidade(Modalidade modalidade) {
        this.modalidade = modalidade;
    }

    public void setCargaHorariaSemanal(int cargaHorariaSemanal) {
        this.cargaHorariaSemanal = cargaHorariaSemanal;
    }

    public void setSituacao(SituacaoVaga situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return "Vaga{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", empresa=" + empresaResponsavel.getNomeEmpresa() +
                ", area=" + area.getNome() +
                ", modalidade=" + modalidade +
                ", cargaHorariaSemanal=" + cargaHorariaSemanal +
                ", situacao=" + situacao +
                '}';
    }
}
