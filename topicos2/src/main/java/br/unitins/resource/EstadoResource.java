package br.unitins.resource;

import java.util.List;

import br.unitins.dto.EstadoRequestDTO;
import br.unitins.model.Estado;
import br.unitins.service.EstadoService;
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

@Path("/estados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EstadoResource {

    @Inject EstadoService estadoService;

    @GET
    public List<Estado> findAll() { return estadoService.findAll(); }

    @GET
    @Path("/{id}")
    public Estado findById(@PathParam("id") Long id) { return estadoService.findById(id); }

    @POST
    public Estado create(@Valid EstadoRequestDTO dto) { return estadoService.create(dto); }

    @PUT
    @Path("/{id}")
    public Estado update(@PathParam("id") Long id, @Valid EstadoRequestDTO dto) { return estadoService.update(id, dto); }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) { estadoService.delete(id); }
}