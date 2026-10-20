package br.unitins.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "carro_imagem")
public class CarroImagem extends DefaultEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carro_id", nullable = false, unique = true)
    private Carro carro;

    @Column(name = "content_type", nullable = false, length = 30)
    private String contentType;

    @Column(name = "dados", nullable = false, columnDefinition = "bytea")
    private byte[] dados;

    public Carro getCarro() { return carro; }
    public void setCarro(Carro carro) { this.carro = carro; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public byte[] getDados() { return dados; }
    public void setDados(byte[] dados) { this.dados = dados; }
}
