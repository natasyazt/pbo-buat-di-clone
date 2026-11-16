package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;

/**
 * Entitas Mata Kuliah yang diampu dosen.
 */
public class MataKuliah implements Validatable {
    private String id;
    private String kode;
    private String nama;
    private int sks;
    private String semester;
    private String kelas;
    private String ruang;
    private String jadwal;
    private String deskripsi;

    public MataKuliah(String id, String kode, String nama, int sks, String semester,
                      String kelas, String ruang, String jadwal, String deskripsi) throws ValidasiInputException {
        setId(id);
        setKode(kode);
        setNama(nama);
        setSks(sks);
        setSemester(semester);
        setKelas(kelas);
        setRuang(ruang);
        setJadwal(jadwal);
        setDeskripsi(deskripsi);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidasiInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidasiInputException("ID mata kuliah tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) throws ValidasiInputException {
        if (kode == null || kode.trim().isEmpty()) {
            throw new ValidasiInputException("Kode mata kuliah tidak boleh kosong.");
        }
        this.kode = kode.trim().toUpperCase();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) throws ValidasiInputException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidasiInputException("Nama mata kuliah tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) throws ValidasiInputException {
        if (sks < 1 || sks > 6) {
            throw new ValidasiInputException("Jumlah SKS harus antara 1 hingga 6.");
        }
        this.sks = sks;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) throws ValidasiInputException {
        if (semester == null || semester.trim().isEmpty()) {
            throw new ValidasiInputException("Semester wajib diisi.");
        }
        this.semester = semester.trim();
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) throws ValidasiInputException {
        if (kelas == null || kelas.trim().isEmpty()) {
            throw new ValidasiInputException("Nama kelas wajib diisi.");
        }
        this.kelas = kelas.trim();
    }

    public String getRuang() {
        return ruang;
    }

    public void setRuang(String ruang) {
        this.ruang = ruang == null ? "" : ruang.trim();
    }

    public String getJadwal() {
        return jadwal;
    }

    public void setJadwal(String jadwal) {
        this.jadwal = jadwal == null ? "" : jadwal.trim();
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi == null ? "" : deskripsi.trim();
    }

    @Override
    public void validasi() throws ValidasiInputException {
        if (kode == null || kode.isEmpty() || nama == null || nama.isEmpty()) {
            throw new ValidasiInputException("Kode dan nama mata kuliah wajib diisi.");
        }
        if (sks < 1 || sks > 6) {
            throw new ValidasiInputException("SKS harus 1-6.");
        }
    }

    @Override
    public String toString() {
        return kode + " - " + nama + " (" + kelas + ")";
    }
}
