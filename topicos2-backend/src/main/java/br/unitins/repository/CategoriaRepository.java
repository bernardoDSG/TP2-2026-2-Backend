package br.unitins.repository;

import br.unitins.model.Categoria;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoriaRepository implements PanacheRepository<Categoria> {

    public PanacheQuery<Categoria> findByNome(String nome) {
        return find("SELECT c FROM Categoria c WHERE LOWER(c.nome) LIKE LOWER(?1) ORDER BY c.nome", "%" + nome + "%");
    }

    @Override
    public PanacheQuery<Categoria> findAll() {
        return find("SELECT c FROM Categoria c ORDER BY c.nome");
    }
}
