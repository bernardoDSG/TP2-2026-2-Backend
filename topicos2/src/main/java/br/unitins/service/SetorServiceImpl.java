package br.unitins.service;

import java.util.List;

import br.unitins.dto.SetorRequestDTO;
import br.unitins.model.Setor;
import br.unitins.repository.SetorRepository;
import br.unitins.repository.VagaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class SetorServiceImpl implements SetorService {

    @Inject SetorRepository setorRepository;
    @Inject VagaRepository vagaRepository;

    @Override
    public List<Setor> findAll() {
        return setorRepository.find("order by nome").list();
    }

    @Override
    public Setor findById(Long id) {
        Setor setor = setorRepository.findById(id);
        if (setor == null) throw new NotFoundException("Setor não encontrado.");
        return setor;
    }

    @Override
    @Transactional
    public Setor create(SetorRequestDTO dto) {
        ensureUniqueName(dto.nome(), null);
        Setor setor = new Setor();
        applyRequest(setor, dto);
        setorRepository.persist(setor);
        return setor;
    }

    @Override
    @Transactional
    public Setor update(Long id, SetorRequestDTO dto) {
        Setor setor = findById(id);
        if (vagaRepository.count("setor.id = ?1 and (linha > ?2 or coluna > ?3)", id, dto.linhas(), dto.colunas()) > 0) {
            throw new WebApplicationException(
                    "A nova dimensão não comporta as vagas cadastradas neste setor.",
                    Response.Status.CONFLICT);
        }
        ensureUniqueName(dto.nome(), id);
        applyRequest(setor, dto);
        return setor;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Setor setor = findById(id);
        if (vagaRepository.count("setor.id", id) > 0) {
            throw new WebApplicationException("Remova as vagas deste setor antes de excluí-lo.", Response.Status.CONFLICT);
        }
        setorRepository.delete(setor);
    }

    private void applyRequest(Setor setor, SetorRequestDTO dto) {
        setor.setNome(dto.nome().trim());
        setor.setLinhas(dto.linhas());
        setor.setColunas(dto.colunas());
    }

    private void ensureUniqueName(String nome, Long excludedId) {
        String query = "lower(nome) = lower(?1)";
        if (excludedId != null) query += " and id <> ?2";
        Object[] params = excludedId == null ? new Object[] { nome.trim() } : new Object[] { nome.trim(), excludedId };
        if (setorRepository.find(query, params).firstResult() != null) {
            throw new WebApplicationException("Já existe um setor com esse nome.", Response.Status.CONFLICT);
        }
    }
}
