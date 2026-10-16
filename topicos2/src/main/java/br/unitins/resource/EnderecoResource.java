package br.unitins.resource;

import java.util.List;

import br.unitins.dto.EnderecoRequestDTO;
import br.unitins.dto.EnderecoResponseDTO;
import br.unitins.service.EnderecoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/enderecos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnderecoResource {

    @Inject EnderecoService enderecoService;

    @GET
    public List<EnderecoResponseDTO> findAll() {
        return enderecoService.findAll().stream().map(EnderecoResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public EnderecoResponseDTO findById(@PathParam("id") Long id) {
        return EnderecoResponseDTO.valueOf(enderecoService.findById(id));
    }

    @POST
    public EnderecoResponseDTO create(@Valid EnderecoRequestDTO dto) {
        return EnderecoResponseDTO.valueOf(enderecoService.create(dto));
    }

    @PUT
    @Path("/{id}")
    public EnderecoResponseDTO update(@PathParam("id") Long id, @Valid EnderecoRequestDTO dto) {
        return EnderecoResponseDTO.valueOf(enderecoService.update(id, dto));
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        enderecoService.delete(id);
    }
}
