package br.unitins.service;

import java.util.List;

import br.unitins.dto.MunicipioRequestDTO;
import br.unitins.model.Estado;
import br.unitins.model.Municipio;
import br.unitins.repository.EnderecoRepository;
import br.unitins.repository.EstadoRepository;
import br.unitins.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class MunicipioServiceImpl implements MunicipioService {

    @Inject MunicipioRepository municipioRepository;
    @Inject EstadoRepository estadoRepository;
    @Inject EnderecoRepository enderecoRepository;

    @Override
    public List<Municipio> findAll() {
        return municipioRepository.find("order by estado.nome, nome").list();
    }

    @Override
    public Municipio findById(Long id) {
        Municipio municipio = municipioRepository.findById(id);
        if (municipio == null) throw new NotFoundException("Município não encontrado.");
        return municipio;
    }

    @Override
    @Transactional
    public Municipio create(MunicipioRequestDTO dto) {
        Estado estado = estadoRepository.findById(dto.estadoId());
        if (estado == null) throw new BadRequestException("Estado não encontrado.");
        ensureUnique(dto.nome(), estado, null);
        Municipio municipio = new Municipio();
        municipio.setNome(dto.nome().trim());
        municipio.setEstado(estado);
        municipioRepository.persist(municipio);
        return municipio;
    }

    @Override
    @Transactional
    public Municipio update(Long id, MunicipioRequestDTO dto) {
        Municipio municipio = findById(id);
        Estado estado = estadoRepository.findById(dto.estadoId());
        if (estado == null) throw new BadRequestException("Estado não encontrado.");
        ensureUnique(dto.nome(), estado, id);
        municipio.setNome(dto.nome().trim());
        municipio.setEstado(estado);
        return municipio;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Municipio municipio = findById(id);
        if (enderecoRepository.count("municipio.id", id) > 0) {
            throw new WebApplicationException("Este município está associado a endereços de clientes.", Response.Status.CONFLICT);
        }
        municipioRepository.delete(municipio);
    }

    private void ensureUnique(String nome, Estado estado, Long excludedId) {
        String query = "lower(nome) = lower(?1) and estado.id = ?2";
        if (excludedId != null) query += " and id <> ?3";
        Object[] params = excludedId == null
                ? new Object[] { nome.trim(), estado.getId() }
                : new Object[] { nome.trim(), estado.getId(), excludedId };
        if (municipioRepository.find(query, params).firstResult() != null) {
            throw new WebApplicationException("Este município já está cadastrado neste estado.", Response.Status.CONFLICT);
        }
    }
}