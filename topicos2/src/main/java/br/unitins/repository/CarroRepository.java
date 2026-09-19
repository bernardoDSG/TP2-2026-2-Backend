package br.unitins.repository;

import br.unitins.model.Carro;
import br.unitins.model.StatusUso;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarroRepository implements PanacheRepository<Carro> {

    public PanacheQuery<Carro> findByNome(String nome) {
        return find("SELECT c FROM Carro c WHERE LOWER(c.nome) LIKE LOWER(?1)", "%" + nome + "%");
    }

    public PanacheQuery<Carro> findByStatusUso(StatusUso statusUso) {
        return find("SELECT c FROM Carro c WHERE c.statusUso = ?1 ORDER BY c.nome", statusUso);
    }

    public PanacheQuery<Carro> findByCor(Long corId) {
        return find("SELECT c FROM Carro c WHERE c.cor.id = ?1 ORDER BY c.nome", corId);
    }

    @Override
    public PanacheQuery<Carro> findAll() {
        
        return find("SELECT c FROM Carro c ORDER BY c.nome");
    }
    
}
