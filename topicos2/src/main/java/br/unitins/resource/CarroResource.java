package br.unitins.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import br.unitins.dto.CarroRequestDTO;
import br.unitins.dto.CarroResponseDTO;
import br.unitins.model.Carro;
import br.unitins.model.CarroImagem;
import br.unitins.model.StatusUso;
import br.unitins.service.CarroService;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
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
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;


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

    @POST
    @Path("/{id}/imagem")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImagem(@PathParam("id") Long id, @RestForm("imagem") FileUpload imagem) throws IOException {
        if (imagem == null || imagem.uploadedFile() == null) {
            throw new BadRequestException("Selecione uma imagem para enviar.");
        }
        long size = Files.size(imagem.uploadedFile());
        if (size > 5 * 1024 * 1024) {
            throw new BadRequestException("A imagem deve ter no máximo 5 MB.");
        }
        carroService.saveImagem(id, Files.readAllBytes(imagem.uploadedFile()), imagem.contentType());
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/imagem")
    @Produces({ "image/jpeg", "image/png", "image/gif", "image/webp" })
    public Response buscarImagem(@PathParam("id") Long id) {
        CarroImagem imagem = carroService.findImagem(id);
        return Response.ok(imagem.getDados())
                .type(imagem.getContentType())
                .header("Cache-Control", "no-store")
                .build();
    }

    @DELETE
    @Path("/{id}/imagem")
    public void excluirImagem(@PathParam("id") Long id) {
        carroService.deleteImagem(id);
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
