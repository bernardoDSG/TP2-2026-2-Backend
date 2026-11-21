package br.unitins;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import br.unitins.service.ObjectStorageService;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
@IfBuildProfile("test")
public class InMemoryObjectStorageService implements ObjectStorageService {

    private final ConcurrentMap<String, byte[]> objects = new ConcurrentHashMap<>();

    @Override
    public void putObject(String key, byte[] dados, String contentType) {
        objects.put(key, dados.clone());
    }

    @Override
    public byte[] getObject(String key) {
        byte[] dados = objects.get(key);
        if (dados == null) {
            throw new NotFoundException("Imagem não encontrada no armazenamento.");
        }
        return dados.clone();
    }

    @Override
    public void deleteObject(String key) {
        objects.remove(key);
    }

    boolean containsObject(String key) {
        return objects.containsKey(key);
    }
}
