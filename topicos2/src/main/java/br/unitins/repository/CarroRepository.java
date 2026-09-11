package br.unitins.repository;

import br.unitins.model.Carro;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarroRepository implements PanacheRepository<Carro> {

    public PanacheQuery<Carro> findByNome(String nome) {
        return find("SELECT c FROM Carro c WHERE c.nome LIKE ?1 ", "%" + nome + "%");
    }

    @Override
    public PanacheQuery<Carro> findAll() {
        
        return find("SELECT c FROM Carro c ORDER BY c.nome");
    }
    
}
