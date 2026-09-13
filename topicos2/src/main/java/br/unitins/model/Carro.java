package br.unitins.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity 
public class Carro extends DefaultEntity{
    private String nome;

    private StatusUso statusUso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cor_id", nullable = false)
    private Cor cor;

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

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    
    
}
