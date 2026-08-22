package comissiones.Charllotte.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Produto {

    @Id
    long Id;

    String produto;
    String codigodoproduto;
    double valor;
    double quantidade;



    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getCodigodoproduto() {
        return codigodoproduto;
    }

    public void setCodigodoproduto(String codigodoproduto) {
        this.codigodoproduto = codigodoproduto;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
}
