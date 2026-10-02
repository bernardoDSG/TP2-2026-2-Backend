package br.unitins.service;

import java.util.ArrayList;
import java.util.List;

import br.unitins.dto.ClienteRequestDTO;
import br.unitins.dto.EnderecoRequestDTO;
import br.unitins.model.Cliente;
import br.unitins.model.Endereco;
import br.unitins.model.Municipio;
import br.unitins.repository.ClienteRepository;
import br.unitins.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ClienteServiceImpl implements ClienteService {

    @Inject ClienteRepository clienteRepository;
    @Inject MunicipioRepository municipioRepository;

    @Override
    public List<Cliente> findAll(Integer page, Integer pageSize) {
        return clienteRepository.findAll().page(page, pageSize).list();
    }

    @Override
    public Cliente findById(Long id) {
        Cliente cliente = clienteRepository.findById(id);
        if (cliente == null) throw new NotFoundException("Cliente não encontrado.");
        return cliente;
    }

    @Override
    @Transactional
    public Cliente create(ClienteRequestDTO dto) {
        validateCpf(dto, null);
        Cliente cliente = new Cliente();
        applyRequest(cliente, dto);
        clienteRepository.persist(cliente);
        return cliente;
    }

    @Override
    @Transactional
    public Cliente update(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id);
        if (cliente == null) throw new NotFoundException("Cliente não encontrado.");
        validateCpf(dto, id);
        applyRequest(cliente, dto);
        return cliente;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!clienteRepository.deleteById(id)) throw new NotFoundException("Cliente não encontrado.");
    }

    private void validateCpf(ClienteRequestDTO dto, Long currentId) {
        if (!isValidCpf(dto.cpf())) throw new BadRequestException("CPF inválido.");
        Object existing = currentId == null
                ? clienteRepository.find("cpf", dto.cpf()).firstResult()
                : clienteRepository.find("cpf = ?1 and id <> ?2", dto.cpf(), currentId).firstResult();
        if (existing != null) throw new WebApplicationException("CPF já cadastrado.", Response.Status.CONFLICT);
    }

    private void applyRequest(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNome(dto.nome().trim());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email().trim().toLowerCase());
        cliente.setTelefone(dto.telefone());

        List<Endereco> enderecos = new ArrayList<>();
        for (EnderecoRequestDTO enderecoDto : dto.enderecos()) {
            Municipio municipio = municipioRepository.findById(enderecoDto.municipioId());
            if (municipio == null) throw new BadRequestException("Município não encontrado.");

            Endereco endereco = new Endereco();
            endereco.setCep(enderecoDto.cep());
            endereco.setLogradouro(enderecoDto.logradouro().trim());
            endereco.setNumero(enderecoDto.numero().trim());
            endereco.setComplemento(enderecoDto.complemento() == null ? null : enderecoDto.complemento().trim());
            endereco.setBairro(enderecoDto.bairro().trim());
            endereco.setMunicipio(municipio);
            enderecos.add(endereco);
        }
        cliente.setEnderecos(enderecos);
    }

    private boolean isValidCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        int firstDigit = cpfDigit(cpf, 9, 10);
        int secondDigit = cpfDigit(cpf, 10, 11);
        return cpf.charAt(9) - '0' == firstDigit && cpf.charAt(10) - '0' == secondDigit;
    }

    private int cpfDigit(String cpf, int length, int weightStart) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (cpf.charAt(i) - '0') * (weightStart - i);
        }
        int remainder = (sum * 10) % 11;
        return remainder == 10 ? 0 : remainder;
    }
}