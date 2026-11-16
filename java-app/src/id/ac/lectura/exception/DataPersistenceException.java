package id.ac.lectura.exception;

/**
 * Custom checked exception untuk menangani kegagalan baca/tulis
 * pada lapisan penyimpanan permanen (file/database).
 */
public class DataPersistenceException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataPersistenceException(String message) {
        super(message);
    }

    public DataPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
