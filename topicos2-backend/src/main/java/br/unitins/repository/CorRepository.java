package br.unitins.repository;

import br.unitins.model.Cor;
import br.unitins.model.Tonalidade;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CorRepository implements PanacheRepository<Cor> {

    public PanacheQuery<Cor> findByNome(String nome) {
        return find("SELECT c FROM Cor c WHERE LOWER(c.nome) LIKE LOWER(?1)", "%" + nome + "%");
    }

    public PanacheQuery<Cor> findByTonalidade(Tonalidade tonalidade) {
        return find("SELECT c FROM Cor c WHERE c.tonalidade = ?1 ORDER BY c.nome", tonalidade);
    }

    

    @Override
    public PanacheQuery<Cor> findAll() {
        
        return find("SELECT c FROM Cor c ORDER BY c.nome");
    }
    
}
