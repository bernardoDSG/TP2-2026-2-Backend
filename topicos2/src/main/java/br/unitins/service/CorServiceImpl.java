package br.unitins.service;

import java.util.List;

import br.unitins.dto.CorRequestDTO;
import br.unitins.model.Cor;
import br.unitins.model.Tonalidade;
import br.unitins.repository.CorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CorServiceImpl implements CorService {

    @Inject
    CorRepository corRepository;    

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
    public void delete(Long id) {
        Cor cor = corRepository.findById(id);
        if (cor == null)
            return;
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
    @Transactional
    public void update(Long id, CorRequestDTO dto) {
        Cor cor = corRepository.findById(id);
        if (cor == null)
            return;
        cor.setNome(dto.nome());
        cor.setTonalidade(Tonalidade.valueOf(dto.tonalidadeId()));
        
    }

 

}
