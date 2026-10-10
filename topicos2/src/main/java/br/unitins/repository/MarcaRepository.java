package br.unitins.repository;

import br.unitins.model.Marca;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MarcaRepository implements PanacheRepository<Marca> {

    public PanacheQuery<Marca> findByNome(String nome) {
        return find("SELECT m FROM Marca m WHERE LOWER(m.nome) LIKE LOWER(?1) ORDER BY m.nome", "%" + nome + "%");
    }

    @Override
    public PanacheQuery<Marca> findAll() {
        return find("SELECT m FROM Marca m ORDER BY m.nome");
    }
}
