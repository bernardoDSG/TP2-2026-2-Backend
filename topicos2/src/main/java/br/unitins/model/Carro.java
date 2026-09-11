package br.unitins.model;

import jakarta.persistence.Entity;

@Entity 
public class Carro extends DefaultEntity{
    private String nome;

    private StatusUso statusUso;

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

    
    
}
