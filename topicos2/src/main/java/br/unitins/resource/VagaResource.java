package br.unitins.resource;

import java.util.List;

import br.unitins.dto.VagaRequestDTO;
import br.unitins.dto.VagaResponseDTO;
import br.unitins.service.VagaService;
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

@Path("/vagas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VagaResource {

    @Inject VagaService vagaService;

    @GET
    public List<VagaResponseDTO> findAll() {
        return vagaService.findAll().stream().map(VagaResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public VagaResponseDTO findById(@PathParam("id") Long id) {
        return VagaResponseDTO.valueOf(vagaService.findById(id));
    }

    @POST
    public VagaResponseDTO create(@Valid VagaRequestDTO dto) {
        return VagaResponseDTO.valueOf(vagaService.create(dto));
    }

    @PUT
    @Path("/{id}")
    public VagaResponseDTO update(@PathParam("id") Long id, @Valid VagaRequestDTO dto) {
        return VagaResponseDTO.valueOf(vagaService.update(id, dto));
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        vagaService.delete(id);
    }
}
