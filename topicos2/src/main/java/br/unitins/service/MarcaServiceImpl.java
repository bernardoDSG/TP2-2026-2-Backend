package br.unitins.service;

import java.util.List;

import br.unitins.dto.MarcaRequestDTO;
import br.unitins.model.Marca;
import br.unitins.repository.CarroRepository;
import br.unitins.repository.MarcaRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class MarcaServiceImpl implements MarcaService {

    @Inject
    MarcaRepository marcaRepository;

    @Inject
    CarroRepository carroRepository;

    @Override
    public List<Marca> findAll(Integer page, Integer pageSize) {
        PanacheQuery<Marca> query = marcaRepository.findAll().page(page, pageSize);
        return query.list();
    }

    @Override
    public List<Marca> findByNome(String nome, Integer page, Integer pageSize) {
        return marcaRepository.findByNome(nome).page(page, pageSize).list();
    }

    @Override
    public Marca findById(Long id) {
        return marcaRepository.findById(id);
    }

    @Override
    @Transactional
    public Marca create(MarcaRequestDTO dto) {
        Marca marca = new Marca();
        marca.setNome(dto.nome());
        marcaRepository.persist(marca);
        return marca;
    }

    @Override
    @Transactional
    public void update(Long id, MarcaRequestDTO dto) {
        Marca marca = marcaRepository.findById(id);
        if (marca == null) {
            throw new NotFoundException("Marca não encontrada.");
        }
        marca.setNome(dto.nome());
    }

    @Override
    @Transactional
    public void delete(Long id, Long replacementMarcaId) {
        Marca marca = marcaRepository.findById(id);
        if (marca == null) {
            throw new NotFoundException("Marca não encontrada.");
        }

        long linkedCars = carroRepository.count("marca.id", id);
        if (linkedCars > 0) {
            if (replacementMarcaId == null) {
                throw new WebApplicationException(
                        "A marca está vinculada a carros. Informe uma marca substituta.",
                        Response.Status.CONFLICT);
            }
            if (replacementMarcaId.equals(id)) {
                throw new BadRequestException("Escolha uma marca diferente da que será excluída.");
            }
            Marca replacement = marcaRepository.findById(replacementMarcaId);
            if (replacement == null) {
                throw new BadRequestException("Marca substituta não encontrada.");
            }
            carroRepository.update("marca = ?1 where marca.id = ?2", replacement, id);
        }
        marcaRepository.delete(marca);
    }
}
