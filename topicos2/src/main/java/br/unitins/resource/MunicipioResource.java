package br.unitins.resource;

import java.util.List;

import br.unitins.dto.MunicipioRequestDTO;
import br.unitins.model.Municipio;
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
    public List<Municipio> findAll() { return municipioService.findAll(); }

    @GET
    @Path("/{id}")
    public Municipio findById(@PathParam("id") Long id) { return municipioService.findById(id); }

    @POST
    public Municipio create(@Valid MunicipioRequestDTO dto) { return municipioService.create(dto); }

    @PUT
    @Path("/{id}")
    public Municipio update(@PathParam("id") Long id, @Valid MunicipioRequestDTO dto) { return municipioService.update(id, dto); }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) { municipioService.delete(id); }
}