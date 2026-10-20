package br.unitins.repository;

import br.unitins.model.Estado;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EstadoRepository implements PanacheRepository<Estado> {
    public Estado findBySigla(String sigla) {
        return find("upper(sigla) = ?1", sigla.toUpperCase()).firstResult();
    }
}