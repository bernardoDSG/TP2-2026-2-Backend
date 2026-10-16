package br.unitins.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Vaga extends DefaultEntity {

    @Column(name = "codigo_vaga", nullable = false, unique = true, length = 40)
    private String codigoVaga;

    @Column(nullable = false)
    private Integer linha;

    @Column(nullable = false)
    private Integer coluna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVaga tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;

    @OneToOne
    @JoinColumn(name = "carro_id", unique = true)
    private Carro carro;

    public String getCodigoVaga() { return codigoVaga; }
    public void setCodigoVaga(String codigoVaga) { this.codigoVaga = codigoVaga; }
    public Integer getLinha() { return linha; }
    public void setLinha(Integer linha) { this.linha = linha; }
    public Integer getColuna() { return coluna; }
    public void setColuna(Integer coluna) { this.coluna = coluna; }
    public TipoVaga getTipo() { return tipo; }
    public void setTipo(TipoVaga tipo) { this.tipo = tipo; }
    public Setor getSetor() { return setor; }
    public void setSetor(Setor setor) { this.setor = setor; }
    public Carro getCarro() { return carro; }
    public void setCarro(Carro carro) { this.carro = carro; }
}
