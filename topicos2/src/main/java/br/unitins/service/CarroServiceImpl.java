package br.unitins.service;

import java.util.List;

import br.unitins.dto.CarroRequestDTO;
import br.unitins.model.Carro;
import br.unitins.model.Categoria;
import br.unitins.model.Marca;
import br.unitins.model.StatusUso;
import br.unitins.repository.CarroRepository;
import br.unitins.repository.CategoriaRepository;
import br.unitins.repository.CorRepository;
import br.unitins.repository.MarcaRepository;
import br.unitins.repository.VagaRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CarroServiceImpl implements CarroService {

    @Inject
    CarroRepository carroRepository;

    @Inject
    CorRepository corRepository;

    @Inject
    MarcaRepository marcaRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    VagaRepository vagaRepository;

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
    public List<Carro> findByStatusUso(StatusUso statusUso, Integer page, Integer pageSize) {
        return carroRepository.findByStatusUso(statusUso).page(page, pageSize).list();
    }

    @Override
    public List<Carro> findByCor(Long corId, Integer page, Integer pageSize) {
        return carroRepository.findByCor(corId).page(page, pageSize).list();
    }

    @Override
    public List<Carro> findByMarca(Long marcaId, Integer page, Integer pageSize) {
        return carroRepository.findByMarca(marcaId).page(page, pageSize).list();
    }

    @Override
    public List<Carro> findByCategoria(Long categoriaId, Integer page, Integer pageSize) {
        return carroRepository.findByCategoria(categoriaId).page(page, pageSize).list();
    }

    @Override
    @Transactional
    public Carro create(CarroRequestDTO dto) {
        Carro carro = new Carro();
        carro.setNome(dto.nome());
        carro.setStatusUso(StatusUso.valueOf(dto.StatusUsoId()));
        carro.setCor(corRepository.findById(dto.corId()));
        carro.setMarca(findMarca(dto.marcaId()));
        carro.setCategoria(findCategoria(dto.categoriaId()));
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
        carro.setStatusUso(StatusUso.valueOf(dto.StatusUsoId()));
        carro.setCor(corRepository.findById(dto.corId()));
        carro.setMarca(findMarca(dto.marcaId()));
        carro.setCategoria(findCategoria(dto.categoriaId()));

    }

    private Marca findMarca(Long marcaId) {
        Marca marca = marcaRepository.findById(marcaId);
        if (marca == null) {
            throw new jakarta.ws.rs.BadRequestException("Marca não encontrada.");
        }
        return marca;
    }

    private Categoria findCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId);
        if (categoria == null) {
            throw new jakarta.ws.rs.BadRequestException("Categoria não encontrada.");
        }
        return categoria;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (vagaRepository.count("carro.id", id) > 0) {
            throw new jakarta.ws.rs.WebApplicationException(
                    "Remova o carro da vaga antes de excluí-lo.",
                    jakarta.ws.rs.core.Response.Status.CONFLICT);
        }
        carroRepository.deleteById(id);

    }

}
