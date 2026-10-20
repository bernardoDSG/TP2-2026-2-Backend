package br.unitins.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity 
public class Carro extends DefaultEntity{
    private String nome;

    private StatusUso statusUso;

    @Column(name = "possui_imagem", nullable = false, columnDefinition = "boolean default false")
    private boolean possuiImagem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cor_id", nullable = false)
    private Cor cor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public StatusUso getStatusUso() {
        return statusUso;
    }

    public void setStatusUso(StatusUso statusUso) {
        this.statusUso = statusUso;
    }

    public boolean isPossuiImagem() {
        return possuiImagem;
    }

    public void setPossuiImagem(boolean possuiImagem) {
        this.possuiImagem = possuiImagem;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
