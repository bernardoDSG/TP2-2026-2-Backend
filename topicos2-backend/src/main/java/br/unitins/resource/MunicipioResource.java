package br.unitins.resource;

import java.util.List;

import br.unitins.dto.MunicipioRequestDTO;
import br.unitins.dto.MunicipioResponseDTO;
import br.unitins.service.MunicipioService;
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

@Path("/municipios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MunicipioResource {

    @Inject MunicipioService municipioService;

    @GET
    public List<MunicipioResponseDTO> findAll() {
        return municipioService.findAll().stream().map(MunicipioResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public MunicipioResponseDTO findById(@PathParam("id") Long id) {
        return MunicipioResponseDTO.valueOf(municipioService.findById(id));
    }

    @POST
    public MunicipioResponseDTO create(@Valid MunicipioRequestDTO dto) {
        return MunicipioResponseDTO.valueOf(municipioService.create(dto));
    }

    @PUT
    @Path("/{id}")
    public MunicipioResponseDTO update(@PathParam("id") Long id, @Valid MunicipioRequestDTO dto) {
        return MunicipioResponseDTO.valueOf(municipioService.update(id, dto));
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) { municipioService.delete(id); }
}