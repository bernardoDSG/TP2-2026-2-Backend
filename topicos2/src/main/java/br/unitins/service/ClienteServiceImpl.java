package br.unitins.service;

import br.unitins.dto.ClienteRequestDTO;
import br.unitins.model.Cliente;
import br.unitins.model.Estado;
import br.unitins.model.Municipio;
import br.unitins.repository.ClienteRepository;
import br.unitins.repository.EstadoRepository;
import br.unitins.repository.MunicipioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ClienteServiceImpl implements ClienteService {

    @Inject ClienteRepository clienteRepository;
    @Inject EstadoRepository estadoRepository;
    @Inject MunicipioRepository municipioRepository;

    @Override
    @Transactional
    public Cliente create(ClienteRequestDTO dto) {
        if (!isValidCpf(dto.cpf())) {
            throw new BadRequestException("CPF inválido.");
        }
        if (clienteRepository.find("cpf", dto.cpf()).firstResult() != null) {
            throw new WebApplicationException("CPF já cadastrado.", Response.Status.CONFLICT);
        }

        Estado estado = estadoRepository.findBySigla(dto.estadoSigla());
        if (estado == null) {
            estado = new Estado();
            estado.setSigla(dto.estadoSigla());
            estado.setNome(dto.estadoNome().trim());
            estadoRepository.persist(estado);
        }

        Municipio municipio = municipioRepository.findByNomeAndEstado(dto.municipio(), estado);
        if (municipio == null) {
            municipio = new Municipio();
            municipio.setNome(dto.municipio().trim());
            municipio.setEstado(estado);
            municipioRepository.persist(municipio);
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome().trim());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email().trim().toLowerCase());
        cliente.setTelefone(dto.telefone());
        cliente.setCep(dto.cep());
        cliente.setLogradouro(dto.logradouro().trim());
        cliente.setNumero(dto.numero().trim());
        cliente.setComplemento(dto.complemento() == null ? null : dto.complemento().trim());
        cliente.setBairro(dto.bairro().trim());
        cliente.setMunicipio(municipio);
        clienteRepository.persist(cliente);
        return cliente;
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