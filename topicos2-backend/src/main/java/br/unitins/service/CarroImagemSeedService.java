package br.unitins.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import br.unitins.model.Carro;
import br.unitins.model.CarroImagem;
import br.unitins.repository.CarroImagemRepository;
import br.unitins.repository.CarroRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CarroImagemSeedService {

    private static final Map<String, String> IMAGENS = Map.of(
            "Argo", "argo.png",
            "Civic", "civic.png",
            "Corolla", "corolla.png",
            "Gol", "gol.png",
            "HB20", "hb20.png",
            "Ka", "ka.png",
            "Kicks", "kicks.png",
            "Kwid", "kwid.png",
            "Onix", "onix.png",
            "Renegade", "renegade.png");

    @Inject
    CarroRepository carroRepository;

    @Inject
    CarroImagemRepository carroImagemRepository;

    @Transactional
    void carregarImagensIniciais(@Observes StartupEvent event) {
        IMAGENS.forEach(this::carregarImagemSeNecessario);
    }

    private void carregarImagemSeNecessario(String nomeCarro, String arquivo) {
        Carro carro = carroRepository.find("nome", nomeCarro).firstResult();
        if (carro == null || carroImagemRepository.find("carro.id", carro.getId()).firstResult() != null) {
            return;
        }

        String resourcePath = "/images/carros/" + arquivo;
        try (var imagemStream = CarroImagemSeedService.class.getResourceAsStream(resourcePath)) {
            if (imagemStream == null) {
                throw new IllegalStateException("Imagem inicial não encontrada no classpath: " + resourcePath);
            }
            CarroImagem imagem = new CarroImagem();
            imagem.setCarro(carro);
            imagem.setContentType("image/png");
            imagem.setDados(imagemStream.readAllBytes());
            carroImagemRepository.persist(imagem);
            carro.setPossuiImagem(true);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível carregar a imagem inicial: " + resourcePath, e);
        }
    }
}
