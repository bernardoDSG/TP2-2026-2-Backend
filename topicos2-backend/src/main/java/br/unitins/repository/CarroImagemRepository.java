package br.unitins.repository;

import br.unitins.model.CarroImagem;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarroImagemRepository implements PanacheRepository<CarroImagem> {}
