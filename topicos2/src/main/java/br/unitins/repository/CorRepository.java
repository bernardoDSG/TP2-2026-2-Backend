package br.unitins.repository;

import br.unitins.model.Cor;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CorRepository implements PanacheRepository<Cor> {

    public PanacheQuery<Cor> findByNome(String nome) {
        return find("SELECT c FROM Cor c WHERE c.nome LIKE ?1 ", "%" + nome + "%");
    }

    

    @Override
    public PanacheQuery<Cor> findAll() {
        
        return find("SELECT c FROM Cor c ORDER BY c.nome");
    }
    
}
