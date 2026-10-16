package br.unitins.repository;

import br.unitins.model.Vaga;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VagaRepository implements PanacheRepository<Vaga> {}
