package br.unitins.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import br.unitins.dto.CarroImagemResponseDTO;
import br.unitins.dto.CarroRequestDTO;
import br.unitins.dto.CarroResponseDTO;
import br.unitins.model.StatusUso;
import br.unitins.service.CarroService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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
@RolesAllowed("ADMIN")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarroResource {
    
    @Inject 
    CarroService carroService;

    @GET
    @PermitAll
    public List<CarroResponseDTO> buscarTodos(
        @QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findAll(page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @PermitAll
    @Path("/nome/{nome}")
    public List<CarroResponseDTO> buscarPorNome(@PathParam("nome") String nome, @QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByNome(nome, page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @PermitAll
    @Path("/status-uso/{statusUsoId}")
    public List<CarroResponseDTO> buscarPorStatusUso(@PathParam("statusUsoId") Long statusUsoId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByStatusUso(StatusUso.valueOf(statusUsoId), page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @PermitAll
    @Path("/cor/{corId}")
    public List<CarroResponseDTO> buscarPorCor(@PathParam("corId") Long corId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByCor(corId, page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @PermitAll
    @Path("/marca/{marcaId}")
    public List<CarroResponseDTO> buscarPorMarca(@PathParam("marcaId") Long marcaId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByMarca(marcaId, page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @PermitAll
    @Path("/categoria/{categoriaId}")
    public List<CarroResponseDTO> buscarPorCategoria(@PathParam("categoriaId") Long categoriaId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return carroService.findByCategoria(categoriaId, page, pageSize).stream().map(CarroResponseDTO::valueOf).toList();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public CarroResponseDTO buscarPorId(@PathParam("id") Long id) {
        return CarroResponseDTO.valueOf(carroService.findById(id));
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
    @PermitAll
    @Path("/{id}/imagem")
    @Produces({ "image/jpeg", "image/png", "image/gif", "image/webp" })
    public Response buscarImagem(@PathParam("id") Long id) {
        return imagemResponse(id, false);
    }

    @GET
    @PermitAll
    @Path("/{id}/imagem/download")
    @Produces({ "image/jpeg", "image/png", "image/gif", "image/webp" })
    public Response baixarImagem(@PathParam("id") Long id) {
        return imagemResponse(id, true);
    }

    private Response imagemResponse(Long id, boolean download) {
        CarroImagemResponseDTO imagem = carroService.findImagem(id);
        Response.ResponseBuilder response = Response.ok(imagem.dados())
                .type(imagem.contentType())
                .header("Cache-Control", "no-store");
        if (download) {
            response.header("Content-Disposition",
                    "attachment; filename=\"carro-" + id + "." + imageExtension(imagem.contentType()) + "\"");
        }
        return response.build();
    }

    private String imageExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> throw new BadRequestException("Formato de imagem não suportado.");
        };
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
