package br.unitins.service;

import java.util.List;

import br.unitins.dto.CarroRequestDTO;
import br.unitins.model.Carro;
import br.unitins.model.StatusUso;
import br.unitins.repository.CarroRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CarroServiceImpl implements CarroService {

    @Inject
    CarroRepository carroRepository;

    @Override
    public List<Carro> findAll(Integer page, Integer pageSize) {
        PanacheQuery<Carro> query = null;

        query = carroRepository.findAll().page(page, pageSize);
        return query.list();
    }

    @Override
    public List<Carro> findByNome(String nome, Integer page, Integer pageSize) {
        return carroRepository.findByNome(nome).page(page, pageSize).list();
    }

    @Override
    @Transactional
    public Carro create(CarroRequestDTO dto) {
        Carro carro = new Carro();
        carro.setNome(dto.nome());
        carro.setStatusUso(StatusUso.valueOf(dto.StatusUsoId()));
        carroRepository.persist(carro);
        return carro;
    }

    @Override
    public Carro findById(Long id) {
        return carroRepository.findById(id);
    }

    @Override
    @Transactional 
    public void update(Long id, CarroRequestDTO dto) {
        Carro carro = carroRepository.findById(id);
        if (carro == null)
            return;
        carro.setNome(dto.nome());

    }

    @Override
    @Transactional
    public void delete(Long id) {
        carroRepository.deleteById(id);

    }

}
