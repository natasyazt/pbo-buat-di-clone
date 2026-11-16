package id.ac.lectura.exception;

/**
 * Custom checked exception untuk menangani kesalahan validasi input pengguna
 * atau pelanggaran aturan bisnis pada entitas akademik.
 */
public class ValidasiInputException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidasiInputException(String message) {
        super(message);
    }

    public ValidasiInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
