package br.unitins.service;

public interface ObjectStorageService {

    void putObject(String key, byte[] dados, String contentType);

    byte[] getObject(String key);

    void deleteObject(String key);
}
