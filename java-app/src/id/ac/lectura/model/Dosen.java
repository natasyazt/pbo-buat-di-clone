package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;

/**
 * Representasi Dosen pengampu mata kuliah.
 * Mewarisi kelas abstrak Person.
 */
public class Dosen extends Person {
    private String nip;
    private String gelar;
    private String prodi;
    private String fakultas;
    private String password;

    public Dosen(String id, String nip, String nama, String gelar, String email,
                 String prodi, String fakultas, String password) throws ValidasiInputException {
        super(id, nama, email);
        setNip(nip);
        setGelar(gelar);
        setProdi(prodi);
        setFakultas(fakultas);
        setPassword(password);
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) throws ValidasiInputException {
        if (nip == null || nip.trim().isEmpty()) {
            throw new ValidasiInputException("NIP dosen tidak boleh kosong.");
        }
        this.nip = nip.trim();
    }

    public String getGelar() {
        return gelar;
    }

    public void setGelar(String gelar) {
        this.gelar = gelar == null ? "" : gelar.trim();
    }

    public String getProdi() {
        return prodi;
    }

    public void setProdi(String prodi) throws ValidasiInputException {
        if (prodi == null || prodi.trim().isEmpty()) {
            throw new ValidasiInputException("Program studi tidak boleh kosong.");
        }
        this.prodi = prodi.trim();
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas == null ? "" : fakultas.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws ValidasiInputException {
        if (password == null || password.length() < 6) {
            throw new ValidasiInputException("Kata sandi minimal 6 karakter.");
        }
        this.password = password;
    }

    @Override
    public String getPeran() {
        return "Dosen Pengampu";
    }

    @Override
    public String getRingkasanIdentitas() {
        return nama + " (NIP: " + nip + " • " + prodi + ")";
    }
}
