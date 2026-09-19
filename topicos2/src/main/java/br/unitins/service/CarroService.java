package br.unitins.service;

import java.util.List;

import br.unitins.dto.CarroRequestDTO;
import br.unitins.model.Carro;
import br.unitins.model.StatusUso;

public interface CarroService {
    
    List<Carro> findAll(Integer page, Integer pageSize);
    List<Carro> findByNome(String nome, Integer page, Integer pageSize);
    List<Carro> findByStatusUso(StatusUso statusUso, Integer page, Integer pageSize);
    List<Carro> findByCor(Long corId, Integer page, Integer pageSize);
    Carro findById(Long id);
    Carro create(CarroRequestDTO dto);
    void update(Long id, CarroRequestDTO dto);
    void delete(Long id);
}
