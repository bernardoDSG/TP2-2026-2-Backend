package br.unitins.service;

import java.util.List;

import br.unitins.dto.CategoriaRequestDTO;
import br.unitins.model.Categoria;
import br.unitins.repository.CarroRepository;
import br.unitins.repository.CategoriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class CategoriaServiceImpl implements CategoriaService {

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    CarroRepository carroRepository;

    @Override
    public List<Categoria> findAll(Integer page, Integer pageSize) {
        return categoriaRepository.findAll().page(page, pageSize).list();
    }

    @Override
    public List<Categoria> findByNome(String nome, Integer page, Integer pageSize) {
        return categoriaRepository.findByNome(nome).page(page, pageSize).list();
    }

    @Override
    public Categoria findById(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria create(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoriaRepository.persist(categoria);
        return categoria;
    }

    @Override
    @Transactional
    public void update(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(id);
        if (categoria == null) {
            throw new NotFoundException("Categoria não encontrada.");
        }
        categoria.setNome(dto.nome());
    }

    @Override
    @Transactional
    public void delete(Long id, Long replacementCategoriaId) {
        Categoria categoria = categoriaRepository.findById(id);
        if (categoria == null) {
            throw new NotFoundException("Categoria não encontrada.");
        }

        long linkedCars = carroRepository.count("categoria.id", id);
        if (linkedCars > 0) {
            if (replacementCategoriaId == null) {
                throw new WebApplicationException(
                        "A categoria está vinculada a carros. Informe uma categoria substituta.",
                        Response.Status.CONFLICT);
            }
            if (replacementCategoriaId.equals(id)) {
                throw new BadRequestException("Escolha uma categoria diferente da que será excluída.");
            }
            Categoria replacement = categoriaRepository.findById(replacementCategoriaId);
            if (replacement == null) {
                throw new BadRequestException("Categoria substituta não encontrada.");
            }
            carroRepository.update("categoria = ?1 where categoria.id = ?2", replacement, id);
        }
        categoriaRepository.delete(categoria);
    }
}
