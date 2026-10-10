package br.unitins.resource;

import java.util.List;

import br.unitins.dto.CarroRequestDTO;
import br.unitins.dto.CarroResponseDTO;
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
    public List<CarroResponseDTO> buscarTodos(
        @QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findAll(page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/nome/{nome}")
    public List<CarroResponseDTO> buscarPorNome(@PathParam("nome") String nome, @QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByNome(nome, page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/status-uso/{statusUsoId}")
    public List<CarroResponseDTO> buscarPorStatusUso(@PathParam("statusUsoId") Long statusUsoId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByStatusUso(StatusUso.valueOf(statusUsoId), page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/cor/{corId}")
    public List<CarroResponseDTO> buscarPorCor(@PathParam("corId") Long corId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByCor(corId, page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/marca/{marcaId}")
    public List<CarroResponseDTO> buscarPorMarca(@PathParam("marcaId") Long marcaId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByMarca(marcaId, page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/categoria/{categoriaId}")
    public List<CarroResponseDTO> buscarPorCategoria(@PathParam("categoriaId") Long categoriaId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByCategoria(categoriaId, page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public CarroResponseDTO buscarPorId(@PathParam("id") Long id) {
        Carro carro = carroService.findById(id);
        return carro == null ? null : CarroResponseDTO.valueOf(carro);
    }

    @POST
    public CarroResponseDTO create(CarroRequestDTO dto) {
        return CarroResponseDTO.valueOf(carroService.create(dto));
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
