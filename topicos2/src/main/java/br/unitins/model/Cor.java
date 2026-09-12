package br.unitins.model;

import jakarta.persistence.Entity;

@Entity
public class Cor extends DefaultEntity{
    private String nome;
    private Tonalidade tonalidade;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Tonalidade getTonalidade() {
        return tonalidade;
    }

    public void setTonalidade(Tonalidade tonalidade) {
        this.tonalidade = tonalidade;
    }
    

}
