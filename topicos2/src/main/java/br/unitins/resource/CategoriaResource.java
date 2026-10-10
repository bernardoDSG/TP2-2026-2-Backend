package br.unitins.resource;

import java.util.List;

import br.unitins.dto.CategoriaRequestDTO;
import br.unitins.dto.CategoriaResponseDTO;
import br.unitins.service.CategoriaService;
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

@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    @Inject
    CategoriaService categoriaService;

    @GET
    public List<CategoriaResponseDTO> buscarTodas(
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return categoriaService.findAll(page, pageSize).stream().map(CategoriaResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/nome/{nome}")
    public List<CategoriaResponseDTO> buscarPorNome(
            @PathParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return categoriaService.findByNome(nome, page, pageSize).stream().map(CategoriaResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public CategoriaResponseDTO buscarPorId(@PathParam("id") Long id) {
        var categoria = categoriaService.findById(id);
        return categoria == null ? null : CategoriaResponseDTO.valueOf(categoria);
    }

    @POST
    public CategoriaResponseDTO create(CategoriaRequestDTO dto) {
        return CategoriaResponseDTO.valueOf(categoriaService.create(dto));
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") Long id, CategoriaRequestDTO dto) {
        categoriaService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(
            @PathParam("id") Long id,
            @QueryParam("replacementCategoriaId") Long replacementCategoriaId) {
        categoriaService.delete(id, replacementCategoriaId);
    }
}
