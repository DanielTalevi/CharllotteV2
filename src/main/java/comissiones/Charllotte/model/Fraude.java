package comissiones.Charllotte.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fraude")
public class Fraude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fraude")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venda", nullable = false)
    private Venda venda;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_identificacao", nullable = false)
    private LocalDateTime dataIdentificacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusFraude status;

    public Fraude() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataIdentificacao() {
        return dataIdentificacao;
    }

    public void setDataIdentificacao(LocalDateTime dataIdentificacao) {
        this.dataIdentificacao = dataIdentificacao;
    }

    public StatusFraude getStatus() {
        return status;
    }

    public void setStatus(StatusFraude status) {
        this.status = status;
    }
}