package br.unitins.resource;

import java.util.List;

import br.unitins.dto.CorRequestDTO;
import br.unitins.model.Cor;
import br.unitins.service.CorService;
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

@Path("/cores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CorResource {
    
    @Inject
    CorService corService;

    @GET
    public List<Cor> buscarTodas(@QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("100") Integer pageSize) {
        return corService.findAll(page, pageSize);
    }

    @GET
    @Path("/nome/{nome}")
    public List<Cor> buscarPorNome(@QueryParam("nome") String nome, @QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("100") Integer pageSize) {
        return corService.findByNome(nome, page, pageSize);
    }

    @GET
    @Path("/{id}")
    public Cor buscarPorId(@QueryParam("id") Long id) {
        return corService.findById(id);
    }

    @POST
    public Cor create(CorRequestDTO cor) {
        return corService.create(cor);
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") Long id, CorRequestDTO cor) {
        corService.update(id, cor);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        corService.delete(id);
    }

}
