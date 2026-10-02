package br.unitins.service;

import java.util.List;

import br.unitins.dto.CorRequestDTO;
import br.unitins.model.Cor;
import br.unitins.model.Tonalidade;
import br.unitins.repository.CarroRepository;
import br.unitins.repository.CorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class CorServiceImpl implements CorService {

    @Inject
    CorRepository corRepository;    
    @Inject
    CarroRepository carroRepository;

    @Override
    @Transactional
    public Cor create(CorRequestDTO dto) {
        Cor cor = new Cor();
        cor.setNome(dto.nome());
        cor.setTonalidade(Tonalidade.valueOf(dto.tonalidadeId()));
        corRepository.persist(cor);
        return cor;
    }

    @Override
    @Transactional
    public void delete(Long id, Long replacementColorId) {
        Cor cor = corRepository.findById(id);
        if (cor == null) throw new NotFoundException("Cor não encontrada.");

        long linkedCars = carroRepository.count("cor.id", id);
        if (linkedCars > 0) {
            if (replacementColorId == null) {
                throw new WebApplicationException(
                        "A cor está vinculada a carros. Informe uma cor substituta.",
                        Response.Status.CONFLICT);
            }
            if (replacementColorId.equals(id)) {
                throw new BadRequestException("Escolha uma cor diferente da que será excluída.");
            }
            Cor replacement = corRepository.findById(replacementColorId);
            if (replacement == null) throw new BadRequestException("Cor substituta não encontrada.");
            carroRepository.update("cor = ?1 where cor.id = ?2", replacement, id);
        }
        corRepository.delete(cor);
    }

    @Override
    public List<Cor> findAll(Integer page, Integer pageSize) {
        
        return corRepository.findAll().page(page, pageSize).list();
    }

    @Override
    public Cor findById(Long id) {
        return corRepository.findById(id);
    }

    @Override
    public List<Cor> findByNome(String nome, Integer page, Integer pageSize) {
        return corRepository.findByNome(nome).page(page, pageSize).list();
    }

    @Override
    public List<Cor> findByTonalidade(Tonalidade tonalidade, Integer page, Integer pageSize) {
        return corRepository.findByTonalidade(tonalidade).page(page, pageSize).list();
    }

    @Override
    @Transactional
    public void update(Long id, CorRequestDTO dto) {
        Cor cor = corRepository.findById(id);
        if (cor == null)
            return;
        cor.setNome(dto.nome());
        cor.setTonalidade(Tonalidade.valueOf(dto.tonalidadeId()));
        
    }

 

}
