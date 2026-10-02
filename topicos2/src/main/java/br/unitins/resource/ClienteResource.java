package br.unitins.resource;

import br.unitins.dto.ClienteRequestDTO;
import br.unitins.model.Cliente;
import br.unitins.service.ClienteService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    @POST
    public Cliente create(@Valid ClienteRequestDTO dto) {
        return clienteService.create(dto);
    }
}