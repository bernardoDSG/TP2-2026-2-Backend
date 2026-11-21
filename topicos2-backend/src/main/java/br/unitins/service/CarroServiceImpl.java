package br.unitins.service;

import java.util.List;

import br.unitins.dto.CarroRequestDTO;
import br.unitins.model.Carro;
import br.unitins.model.CarroImagem;
import br.unitins.model.Categoria;
import br.unitins.model.Cor;
import br.unitins.model.Marca;
import br.unitins.model.StatusUso;
import br.unitins.repository.CarroRepository;
import br.unitins.repository.CarroImagemRepository;
import br.unitins.repository.CategoriaRepository;
import br.unitins.repository.CorRepository;
import br.unitins.repository.MarcaRepository;
import br.unitins.repository.VagaRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class CarroServiceImpl implements CarroService {

    @Inject
    CarroRepository carroRepository;

    @Inject
    CarroImagemRepository carroImagemRepository;

    @Inject
    CorRepository corRepository;

    @Inject
    MarcaRepository marcaRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    VagaRepository vagaRepository;

    @Override
    public List<Carro> findAll(Integer page, Integer pageSize) {
        PanacheQuery<Carro> query = null;

        query = carroRepository.findAll().page(page, pageSize);
        return query.list();
    }

    @Override
    public List<Carro> findByNome(String nome, Integer page, Integer pageSize) {
        return carroRepository.findByNome(nome).page(page, pageSize).list();
    }

    @Override
    public List<Carro> findByStatusUso(StatusUso statusUso, Integer page, Integer pageSize) {
        return carroRepository.findByStatusUso(statusUso).page(page, pageSize).list();
    }

    @Override
    public List<Carro> findByCor(Long corId, Integer page, Integer pageSize) {
        return carroRepository.findByCor(corId).page(page, pageSize).list();
    }

    @Override
    public List<Carro> findByMarca(Long marcaId, Integer page, Integer pageSize) {
        return carroRepository.findByMarca(marcaId).page(page, pageSize).list();
    }

    @Override
    public List<Carro> findByCategoria(Long categoriaId, Integer page, Integer pageSize) {
        return carroRepository.findByCategoria(categoriaId).page(page, pageSize).list();
    }

    @Override
    @Transactional
    public Carro create(CarroRequestDTO dto) {
        Carro carro = new Carro();
        aplicarDados(carro, dto);
        carroRepository.persist(carro);
        return carro;
    }

    @Override
    public Carro findById(Long id) {
        return carroRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Carro não encontrado."));
    }

    @Override
    public CarroImagem findImagem(Long id) {
        if (carroRepository.findById(id) == null) throw new NotFoundException("Carro não encontrado.");
        CarroImagem imagem = carroImagemRepository.find("carro.id", id).firstResult();
        if (imagem == null) throw new NotFoundException("Este carro não possui imagem.");
        return imagem;
    }

    @Override
    @Transactional
    public void saveImagem(Long id, byte[] dados, String contentType) {
        Carro carro = buscarParaAlterarImagem(id);
        validateImage(dados, contentType);
        CarroImagem imagem = carroImagemRepository.find("carro.id", id).firstResult();
        if (imagem == null) {
            imagem = new CarroImagem();
            imagem.setCarro(carro);
        }
        imagem.setContentType(contentType);
        imagem.setDados(dados);
        if (imagem.getId() == null) carroImagemRepository.persist(imagem);
        carro.setPossuiImagem(true);
    }

    @Override
    @Transactional
    public void deleteImagem(Long id) {
        Carro carro = buscarParaAlterarImagem(id);
        carroImagemRepository.delete("carro.id", id);
        carro.setPossuiImagem(false);
    }

    private void validateImage(byte[] dados, String contentType) {
        if (dados == null || dados.length == 0 || dados.length > 5 * 1024 * 1024) {
            throw new WebApplicationException("A imagem deve ter no máximo 5 MB.", Response.Status.BAD_REQUEST);
        }
        boolean validImage = contentType != null && switch (contentType) {
            case "image/jpeg" -> dados.length >= 3 && (dados[0] & 0xff) == 0xff
                    && (dados[1] & 0xff) == 0xd8 && (dados[2] & 0xff) == 0xff;
            case "image/png" -> dados.length >= 8 && (dados[0] & 0xff) == 0x89
                    && dados[1] == 0x50 && dados[2] == 0x4e && dados[3] == 0x47
                    && dados[4] == 0x0d && dados[5] == 0x0a && dados[6] == 0x1a && dados[7] == 0x0a;
            case "image/gif" -> dados.length >= 6 && new String(dados, 0, 6, java.nio.charset.StandardCharsets.US_ASCII).matches("GIF87a|GIF89a");
            case "image/webp" -> dados.length >= 12
                    && new String(dados, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF")
                    && new String(dados, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP");
            default -> false;
        };
        if (!validImage) throw new BadRequestException("Envie uma imagem JPEG, PNG, GIF ou WebP válida.");
    }

    @Override
    @Transactional 
    public void update(Long id, CarroRequestDTO dto) {
        aplicarDados(findById(id), dto);
    }

    private Carro buscarParaAlterarImagem(Long id) {
        return carroRepository.findByIdOptional(id, LockModeType.PESSIMISTIC_WRITE)
                .orElseThrow(() -> new NotFoundException("Carro não encontrado."));
    }

    private void aplicarDados(Carro carro, CarroRequestDTO dto) {
        if (dto == null) throw new BadRequestException("Informe os dados do carro.");
        StatusUso status = StatusUso.valueOf(dto.StatusUsoId());
        if (status == null) throw new BadRequestException("Informe um status de uso válido.");
        Cor cor = dto.corId() == null ? null : corRepository.findById(dto.corId());
        if (cor == null) throw new BadRequestException("Cor não encontrada.");
        Marca marca = findMarca(dto.marcaId());
        Categoria categoria = findCategoria(dto.categoriaId());

        carro.setNome(dto.nome());
        carro.setStatusUso(status);
        carro.setCor(cor);
        carro.setMarca(marca);
        carro.setCategoria(categoria);
    }

    private Marca findMarca(Long marcaId) {
        Marca marca = marcaId == null ? null : marcaRepository.findById(marcaId);
        if (marca == null) {
            throw new jakarta.ws.rs.BadRequestException("Marca não encontrada.");
        }
        return marca;
    }

    private Categoria findCategoria(Long categoriaId) {
        Categoria categoria = categoriaId == null ? null : categoriaRepository.findById(categoriaId);
        if (categoria == null) {
            throw new jakarta.ws.rs.BadRequestException("Categoria não encontrada.");
        }
        return categoria;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (vagaRepository.count("carro.id", id) > 0) {
            throw new jakarta.ws.rs.WebApplicationException(
                    "Remova o carro da vaga antes de excluí-lo.",
                    jakarta.ws.rs.core.Response.Status.CONFLICT);
        }
        carroImagemRepository.delete("carro.id", id);
        carroRepository.deleteById(id);

    }

}
