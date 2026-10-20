package br.unitins.service;

import java.util.List;

import br.unitins.dto.EnderecoRequestDTO;
import br.unitins.model.Cliente;
import br.unitins.model.Endereco;
import br.unitins.model.Municipio;
import br.unitins.repository.ClienteRepository;
import br.unitins.repository.EnderecoRepository;
import br.unitins.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class EnderecoServiceImpl implements EnderecoService {

    @Inject EnderecoRepository enderecoRepository;
    @Inject ClienteRepository clienteRepository;
    @Inject MunicipioRepository municipioRepository;

    @Override
    public List<Endereco> findAll() {
        return enderecoRepository.find("order by cliente.nome, logradouro").list();
    }

    @Override
    public Endereco findById(Long id) {
        Endereco endereco = enderecoRepository.findById(id);
        if (endereco == null) throw new NotFoundException("Endereço não encontrado.");
        return endereco;
    }

    @Override
    @Transactional
    public Endereco create(EnderecoRequestDTO dto) {
        Endereco endereco = new Endereco();
        applyRequest(endereco, dto);
        enderecoRepository.persist(endereco);
        return endereco;
    }

    @Override
    @Transactional
    public Endereco update(Long id, EnderecoRequestDTO dto) {
        Endereco endereco = findById(id);
        applyRequest(endereco, dto);
        return endereco;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Endereco endereco = findById(id);
        endereco.getCliente().getEnderecos().remove(endereco);
    }

    private void applyRequest(Endereco endereco, EnderecoRequestDTO dto) {
        if (dto.clienteId() == null) throw new BadRequestException("Cliente é obrigatório para cadastrar o endereço.");
        Cliente cliente = clienteRepository.findById(dto.clienteId());
        if (cliente == null) throw new BadRequestException("Cliente não encontrado.");
        Municipio municipio = municipioRepository.findById(dto.municipioId());
        if (municipio == null) throw new BadRequestException("Município não encontrado.");

        Cliente clienteAnterior = endereco.getCliente();
        if (clienteAnterior != null && !clienteAnterior.getId().equals(cliente.getId())) {
            clienteAnterior.getEnderecos().remove(endereco);
        }
        endereco.setCep(dto.cep());
        endereco.setLogradouro(dto.logradouro().trim());
        endereco.setNumero(dto.numero().trim());
        endereco.setComplemento(dto.complemento() == null ? null : dto.complemento().trim());
        endereco.setBairro(dto.bairro().trim());
        endereco.setMunicipio(municipio);
        endereco.setCliente(cliente);
        if (!cliente.getEnderecos().contains(endereco)) cliente.getEnderecos().add(endereco);
    }
}
