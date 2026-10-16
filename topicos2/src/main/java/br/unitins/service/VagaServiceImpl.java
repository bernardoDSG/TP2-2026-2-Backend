package br.unitins.service;

import java.util.List;

import br.unitins.dto.VagaRequestDTO;
import br.unitins.model.Carro;
import br.unitins.model.Setor;
import br.unitins.model.Vaga;
import br.unitins.repository.CarroRepository;
import br.unitins.repository.SetorRepository;
import br.unitins.repository.VagaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class VagaServiceImpl implements VagaService {

    @Inject VagaRepository vagaRepository;
    @Inject SetorRepository setorRepository;
    @Inject CarroRepository carroRepository;

    @Override
    public List<Vaga> findAll() {
        return vagaRepository.find("order by setor.nome, linha, coluna").list();
    }

    @Override
    public Vaga findById(Long id) {
        Vaga vaga = vagaRepository.findById(id);
        if (vaga == null) throw new NotFoundException("Vaga não encontrada.");
        return vaga;
    }

    @Override
    @Transactional
    public Vaga create(VagaRequestDTO dto) {
        Vaga vaga = new Vaga();
        applyRequest(vaga, dto, null);
        vagaRepository.persist(vaga);
        return vaga;
    }

    @Override
    @Transactional
    public Vaga update(Long id, VagaRequestDTO dto) {
        Vaga vaga = findById(id);
        applyRequest(vaga, dto, id);
        return vaga;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!vagaRepository.deleteById(id)) throw new NotFoundException("Vaga não encontrada.");
    }

    private void applyRequest(Vaga vaga, VagaRequestDTO dto, Long currentId) {
        Setor setor = setorRepository.findById(dto.setorId());
        if (setor == null) throw new BadRequestException("Setor não encontrado.");
        if (dto.linha() > setor.getLinhas() || dto.coluna() > setor.getColunas()) {
            throw new BadRequestException("A posição da vaga deve estar dentro das dimensões do setor.");
        }

        ensureUniqueCode(dto.codigoVaga(), currentId);
        ensureUniquePosition(setor, dto.linha(), dto.coluna(), currentId);

        Carro carro = null;
        if (dto.carroId() != null) {
            carro = carroRepository.findById(dto.carroId());
            if (carro == null) throw new BadRequestException("Carro não encontrado.");
            String query = currentId == null ? "carro.id" : "carro.id = ?1 and id <> ?2";
            Object existing = currentId == null
                    ? vagaRepository.find(query, dto.carroId()).firstResult()
                    : vagaRepository.find(query, dto.carroId(), currentId).firstResult();
            if (existing != null) throw new WebApplicationException("Este carro já está associado a outra vaga.", Response.Status.CONFLICT);
        }

        vaga.setCodigoVaga(dto.codigoVaga().trim());
        vaga.setLinha(dto.linha());
        vaga.setColuna(dto.coluna());
        vaga.setTipo(dto.tipo());
        vaga.setSetor(setor);
        vaga.setCarro(carro);
    }

    private void ensureUniqueCode(String codigo, Long excludedId) {
        String query = "lower(codigoVaga) = lower(?1)";
        if (excludedId != null) query += " and id <> ?2";
        Object[] params = excludedId == null ? new Object[] { codigo.trim() } : new Object[] { codigo.trim(), excludedId };
        if (vagaRepository.find(query, params).firstResult() != null) {
            throw new WebApplicationException("Já existe uma vaga com esse código.", Response.Status.CONFLICT);
        }
    }

    private void ensureUniquePosition(Setor setor, Integer linha, Integer coluna, Long excludedId) {
        String query = "setor.id = ?1 and linha = ?2 and coluna = ?3";
        if (excludedId != null) query += " and id <> ?4";
        Object[] params = excludedId == null
                ? new Object[] { setor.getId(), linha, coluna }
                : new Object[] { setor.getId(), linha, coluna, excludedId };
        if (vagaRepository.find(query, params).firstResult() != null) {
            throw new WebApplicationException("Já existe uma vaga nesta posição do setor.", Response.Status.CONFLICT);
        }
    }
}
