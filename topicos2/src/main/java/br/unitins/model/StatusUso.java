package br.unitins.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusUso {
    SEMINOVO(1L, "Seminovo"),
    USADO(2L, "Usado");

    private final Long ID;
    private final String NOME;
    
    StatusUso(Long id, String nome) {
        this.ID = id;
        this.NOME = nome;
    }

    public Long getId() {
        return ID;
    }

    public String getNome() {
        return NOME;
    }

    public static StatusUso valueOf(Long id) {
        for (StatusUso status : StatusUso.values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return null;
    }

}
