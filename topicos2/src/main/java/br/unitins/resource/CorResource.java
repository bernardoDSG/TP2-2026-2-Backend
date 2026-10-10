package br.unitins.resource;

import java.util.List;

import br.unitins.dto.CorRequestDTO;
import br.unitins.dto.CorResponseDTO;
import br.unitins.model.Cor;
import br.unitins.model.Tonalidade;
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
    public List<CorResponseDTO> buscarTodas(@QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return corService.findAll(page, pageSize).stream().map(CorResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/nome/{nome}")
    public List<CorResponseDTO> buscarPorNome(@PathParam("nome") String nome, @QueryParam("page")  @DefaultValue("0") Integer page, @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return corService.findByNome(nome, page, pageSize).stream().map(CorResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/tonalidade/{tonalidadeId}")
    public List<CorResponseDTO> buscarPorTonalidade(@PathParam("tonalidadeId") Long tonalidadeId,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("pageSize") @DefaultValue("5") Integer pageSize) {
        return corService.findByTonalidade(Tonalidade.valueOf(tonalidadeId), page, pageSize).stream().map(CorResponseDTO::valueOf).toList();
    }

    @GET
    @Path("/{id}")
    public CorResponseDTO buscarPorId(@PathParam("id") Long id) {
        Cor cor = corService.findById(id);
        return cor == null ? null : CorResponseDTO.valueOf(cor);
    }

    @POST
    public CorResponseDTO create(CorRequestDTO cor) {
        return CorResponseDTO.valueOf(corService.create(cor));
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") Long id, CorRequestDTO cor) {
        corService.update(id, cor);
    }

    @DELETE
    @Path("/{id}")
    public void delete(
            @PathParam("id") Long id,
            @QueryParam("replacementColorId") Long replacementColorId) {
        corService.delete(id, replacementColorId);
    }

}
