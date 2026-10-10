package br.unitins.resource;

import java.util.List;

import br.unitins.dto.MarcaRequestDTO;
import br.unitins.model.Marca;
import br.unitins.service.MarcaService;
import jakarta.inject.Inject;
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

@Path("/marcas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MarcaResource {

    @Inject
    MarcaService marcaService;

    @GET
    public List<Marca> buscarTodas(
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return marcaService.findAll(page, pageSize);
    }

    @GET
    @Path("/nome/{nome}")
    public List<Marca> buscarPorNome(
            @PathParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return marcaService.findByNome(nome, page, pageSize);
    }

    @GET
    @Path("/{id}")
    public Marca buscarPorId(@PathParam("id") Long id) {
        return marcaService.findById(id);
    }

    @POST
    public Marca create(MarcaRequestDTO dto) {
        return marcaService.create(dto);
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") Long id, MarcaRequestDTO dto) {
        marcaService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(
            @PathParam("id") Long id,
            @QueryParam("replacementMarcaId") Long replacementMarcaId) {
        marcaService.delete(id, replacementMarcaId);
    }
}
