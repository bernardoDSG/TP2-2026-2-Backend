package br.unitins.resource;

import java.util.List;

import br.unitins.dto.StatusUsoResponseDTO;
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
    public List<StatusUsoResponseDTO> buscarTodos() {
        return statusUsoService.findAll().stream().map(StatusUsoResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public StatusUsoResponseDTO buscarPorId(@PathParam("id") Long id) {
        StatusUso statusUso = statusUsoService.findById(id);
        return statusUso == null ? null : StatusUsoResponseDTO.valueOf(statusUso);
    }
}