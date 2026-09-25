package comissiones.Charllotte.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false, length = 20)
    private String unidade;

    @Column(
            name = "percentual_comissao",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal percentualComissao;

    @Column(nullable = false)
    private Boolean status = true;

    /*
     * =====================================================
     * ESTOQUE
     * =====================================================
     */

    @Column(
            name = "quantidade_estoque",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal quantidadeEstoque = BigDecimal.ZERO;

    @Column(
            name = "estoque_minimo",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;


    public Produto() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }


    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }


    public BigDecimal getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(
            BigDecimal percentualComissao) {

        this.percentualComissao =
                percentualComissao;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    public BigDecimal getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(
            BigDecimal quantidadeEstoque) {

        this.quantidadeEstoque =
                quantidadeEstoque;
    }


    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(
            BigDecimal estoqueMinimo) {

        this.estoqueMinimo =
                estoqueMinimo;
    }
}