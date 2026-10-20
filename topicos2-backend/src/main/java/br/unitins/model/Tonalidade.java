package br.unitins.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Tonalidade {
    CLARO(1L, "Claro"),
    ESCURO(2L, "Escuro"),
    PURO(3L, "Puro");

    private final Long ID;
    private final String NOME;

    Tonalidade(Long id, String nome) {
        this.ID = id;
        this.NOME = nome;
    }

    public Long getID() {
        return ID;
    }

    public String getNOME() {
        return NOME;
    }

    public static Tonalidade valueOf(Long id) {
        for (Tonalidade tonalidade : Tonalidade.values()) {
            if (tonalidade.getID().equals(id)) {
                return tonalidade;
            }
        }
        return null;
    }   
}
