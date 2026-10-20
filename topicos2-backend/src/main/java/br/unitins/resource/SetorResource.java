package br.unitins.resource;

import java.util.List;

import br.unitins.dto.SetorRequestDTO;
import br.unitins.dto.SetorResponseDTO;
import br.unitins.service.SetorService;
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

@Path("/setores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SetorResource {

    @Inject SetorService setorService;

    @GET
    public List<SetorResponseDTO> findAll() {
        return setorService.findAll().stream().map(SetorResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public SetorResponseDTO findById(@PathParam("id") Long id) {
        return SetorResponseDTO.valueOf(setorService.findById(id));
    }

    @POST
    public SetorResponseDTO create(@Valid SetorRequestDTO dto) {
        return SetorResponseDTO.valueOf(setorService.create(dto));
    }

    @PUT
    @Path("/{id}")
    public SetorResponseDTO update(@PathParam("id") Long id, @Valid SetorRequestDTO dto) {
        return SetorResponseDTO.valueOf(setorService.update(id, dto));
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        setorService.delete(id);
    }
}
