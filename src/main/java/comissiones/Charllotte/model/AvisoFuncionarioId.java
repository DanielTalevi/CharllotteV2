package comissiones.Charllotte.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AvisoFuncionarioId implements Serializable {

    @Column(name = "id_aviso")
    private Integer idAviso;

    @Column(name = "id_funcionario")
    private Integer idFuncionario;

    public AvisoFuncionarioId() {
    }

    public AvisoFuncionarioId(
            Integer idAviso,
            Integer idFuncionario) {

        this.idAviso = idAviso;
        this.idFuncionario = idFuncionario;
    }

    public Integer getIdAviso() {
        return idAviso;
    }

    public void setIdAviso(Integer idAviso) {
        this.idAviso = idAviso;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AvisoFuncionarioId)) {
            return false;
        }

        AvisoFuncionarioId outro =
                (AvisoFuncionarioId) obj;

        return Objects.equals(
                    idAviso,
                    outro.idAviso)
                && Objects.equals(
                    idFuncionario,
                    outro.idFuncionario);
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                idAviso,
                idFuncionario);
    }
}