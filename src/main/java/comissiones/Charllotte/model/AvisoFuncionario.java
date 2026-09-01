package comissiones.Charllotte.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "aviso_funcionario")
public class AvisoFuncionario {

    @EmbeddedId
    private AvisoFuncionarioId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idAviso")
    @JoinColumn(name = "id_aviso")
    private Aviso aviso;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idFuncionario")
    @JoinColumn(name = "id_funcionario")
    private Usuario funcionario;

    @Column(nullable = false)
    private Boolean lido = false;

    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;

    public AvisoFuncionario() {
    }

    public AvisoFuncionarioId getId() {
        return id;
    }

    public void setId(AvisoFuncionarioId id) {
        this.id = id;
    }

    public Aviso getAviso() {
        return aviso;
    }

    public void setAviso(Aviso aviso) {
        this.aviso = aviso;
    }

    public Usuario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Usuario funcionario) {
        this.funcionario = funcionario;
    }

    public Boolean getLido() {
        return lido;
    }

    public void setLido(Boolean lido) {
        this.lido = lido;
    }

    public LocalDateTime getDataLeitura() {
        return dataLeitura;
    }

    public void setDataLeitura(LocalDateTime dataLeitura) {
        this.dataLeitura = dataLeitura;
    }
}