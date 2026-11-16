package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;

/**
 * Interface untuk memastikan setiap entitas model dapat memvalidasi
 * integritas datanya sebelum disimpan atau diproses.
 */
public interface Validatable {
    void validasi() throws ValidasiInputException;
}
