package br.unitins.resource;

import java.util.List;

import br.unitins.dto.ClienteRequestDTO;
import br.unitins.dto.ClienteResponseDTO;
import br.unitins.service.ClienteService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    @GET
    public List<ClienteResponseDTO> findAll(
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("100") Integer pageSize) {
        return clienteService.findAll(page, pageSize).stream().map(ClienteResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public ClienteResponseDTO findById(@PathParam("id") Long id) {
        return ClienteResponseDTO.valueOf(clienteService.findById(id));
    }

    @POST
    public ClienteResponseDTO create(@Valid ClienteRequestDTO dto) {
        return ClienteResponseDTO.valueOf(clienteService.create(dto));
    }

    @PUT
    @Path("/{id}")
    public ClienteResponseDTO update(@PathParam("id") Long id, @Valid ClienteRequestDTO dto) {
        return ClienteResponseDTO.valueOf(clienteService.update(id, dto));
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        clienteService.delete(id);
    }
}