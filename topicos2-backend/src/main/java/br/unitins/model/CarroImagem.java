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

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "object_key", nullable = false, unique = true, length = 255)
    private String objectKey;

    public Carro getCarro() { return carro; }
    public void setCarro(Carro carro) { this.carro = carro; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
}
