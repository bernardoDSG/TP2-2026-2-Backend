package br.unitins.repository;

import br.unitins.model.Estado;
import br.unitins.model.Municipio;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MunicipioRepository implements PanacheRepository<Municipio> {
    public Municipio findByNomeAndEstado(String nome, Estado estado) {
        return find("lower(nome) = lower(?1) and estado = ?2", nome.trim(), estado).firstResult();
    }
}