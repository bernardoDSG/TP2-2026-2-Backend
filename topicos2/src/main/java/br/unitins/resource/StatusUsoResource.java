package br.unitins.resource;

import java.util.List;

import br.unitins.model.StatusUso;
import br.unitins.service.StatusUsoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/status-usos")
@Produces(MediaType.APPLICATION_JSON)
public class StatusUsoResource {

    @Inject
    StatusUsoService statusUsoService;

    @GET
    public List<StatusUso> buscarTodos() {
        return statusUsoService.findAll();
    }

    @GET
    @Path("/{id}")
    public StatusUso buscarPorId(@PathParam("id") Long id) {
        return statusUsoService.findById(id);
    }
}