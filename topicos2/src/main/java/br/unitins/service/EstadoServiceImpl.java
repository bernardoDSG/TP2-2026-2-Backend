package br.unitins.service;

import java.util.List;

import br.unitins.dto.EstadoRequestDTO;
import br.unitins.model.Estado;
import br.unitins.repository.EstadoRepository;
import br.unitins.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class EstadoServiceImpl implements EstadoService {

    @Inject EstadoRepository estadoRepository;
    @Inject MunicipioRepository municipioRepository;

    @Override
    public List<Estado> findAll() {
        return estadoRepository.find("order by nome").list();
    }

    @Override
    public Estado findById(Long id) {
        Estado estado = estadoRepository.findById(id);
        if (estado == null) throw new NotFoundException("Estado não encontrado.");
        return estado;
    }

    @Override
    @Transactional
    public Estado create(EstadoRequestDTO dto) {
        String sigla = dto.sigla().trim().toUpperCase();
        if (estadoRepository.findBySigla(sigla) != null) {
            throw new WebApplicationException("Já existe um estado com essa sigla.", Response.Status.CONFLICT);
        }
        Estado estado = new Estado();
        estado.setNome(dto.nome().trim());
        estado.setSigla(sigla);
        estadoRepository.persist(estado);
        return estado;
    }

    @Override
    @Transactional
    public Estado update(Long id, EstadoRequestDTO dto) {
        Estado estado = findById(id);
        String sigla = dto.sigla().trim().toUpperCase();
        if (estadoRepository.find("upper(sigla) = ?1 and id <> ?2", sigla, id).firstResult() != null) {
            throw new WebApplicationException("Já existe um estado com essa sigla.", Response.Status.CONFLICT);
        }
        estado.setNome(dto.nome().trim());
        estado.setSigla(sigla);
        return estado;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Estado estado = findById(id);
        if (municipioRepository.count("estado.id", id) > 0) {
            throw new WebApplicationException("Remova ou transfira os municípios deste estado antes de excluí-lo.", Response.Status.CONFLICT);
        }
        estadoRepository.delete(estado);
    }
}