package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;

/**
 * Kelas abstrak induk untuk entitas manusia dalam sistem akademik (Dosen, Mahasiswa).
 * Menerapkan enkapsulasi dan abstraksi.
 */
public abstract class Person implements Validatable {
    protected String id;
    protected String nama;
    protected String email;

    protected Person(String id, String nama, String email) throws ValidasiInputException {
        setId(id);
        setNama(nama);
        setEmail(email);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidasiInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidasiInputException("ID tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) throws ValidasiInputException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidasiInputException("Nama tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidasiInputException {
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new ValidasiInputException("Format email tidak valid.");
        }
        this.email = email.trim();
    }

    public abstract String getPeran();

    public abstract String getRingkasanIdentitas();

    @Override
    public void validasi() throws ValidasiInputException {
        if (id == null || id.isEmpty()) {
            throw new ValidasiInputException("ID wajib diisi.");
        }
        if (nama == null || nama.isEmpty()) {
            throw new ValidasiInputException("Nama wajib diisi.");
        }
        if (email == null || !email.contains("@")) {
            throw new ValidasiInputException("Email wajib valid.");
        }
    }
}
