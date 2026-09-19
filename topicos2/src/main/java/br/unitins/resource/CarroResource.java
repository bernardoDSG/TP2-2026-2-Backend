package br.unitins.resource;

import java.util.List;

import br.unitins.dto.CarroRequestDTO;
import br.unitins.model.Carro;
import br.unitins.model.StatusUso;
import br.unitins.service.CarroService;
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


@Path("/carros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarroResource {
    
    @Inject 
    CarroService carroService;

    @GET
    public List<Carro> buscarTodos(
        @QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findAll(page, pageSize);
    }

    @GET
    @Path("/nome/{nome}")
    public List<Carro> buscarPorNome(@PathParam("nome") String nome, @QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByNome(nome, page, pageSize);
    }

    @GET
    @Path("/status-uso/{statusUsoId}")
    public List<Carro> buscarPorStatusUso(@PathParam("statusUsoId") Long statusUsoId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByStatusUso(StatusUso.valueOf(statusUsoId), page, pageSize);
    }

    @GET
    @Path("/cor/{corId}")
    public List<Carro> buscarPorCor(@PathParam("corId") Long corId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByCor(corId, page, pageSize);
    }

    @GET
    @Path("/{id}")
    public Carro buscarPorId(@PathParam("id") Long id) {
        return carroService.findById(id);
    }

    @POST
    public Carro create(CarroRequestDTO dto) {
        return carroService.create(dto);
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") Long id, CarroRequestDTO dto) {
        carroService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        carroService.delete(id);
    }


}
