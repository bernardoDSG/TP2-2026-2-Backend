package br.unitins.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Setor extends DefaultEntity {

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false)
    private Integer linhas;

    @Column(nullable = false)
    private Integer colunas;

    @OneToMany(mappedBy = "setor")
    private List<Vaga> vagas = new ArrayList<>();

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getLinhas() { return linhas; }
    public void setLinhas(Integer linhas) { this.linhas = linhas; }
    public Integer getColunas() { return colunas; }
    public void setColunas(Integer colunas) { this.colunas = colunas; }
    public List<Vaga> getVagas() { return vagas; }
    public void setVagas(List<Vaga> vagas) { this.vagas = vagas; }
}
