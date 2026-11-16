package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;

/**
 * Kelas abstrak induk untuk seluruh komponen penilaian akademik.
 * Menerapkan enkapsulasi, abstraksi, dan menjadi dasar polimorfisme
 * untuk perhitungan bobot efektif serta kontribusi nilai.
 */
public abstract class KomponenPenilaian implements Validatable, Evaluatable {
    protected String id;
    protected String mataKuliahId;
    protected String nama;
    protected int bobot;
    protected int kkm;
    protected String keterangan;

    protected KomponenPenilaian(String id, String mataKuliahId, String nama,
                                int bobot, int kkm, String keterangan) throws ValidasiInputException {
        setId(id);
        setMataKuliahId(mataKuliahId);
        setNama(nama);
        setBobot(bobot);
        setKkm(kkm);
        setKeterangan(keterangan);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidasiInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidasiInputException("ID komponen penilaian tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    public String getMataKuliahId() {
        return mataKuliahId;
    }

    public void setMataKuliahId(String mataKuliahId) throws ValidasiInputException {
        if (mataKuliahId == null || mataKuliahId.trim().isEmpty()) {
            throw new ValidasiInputException("ID mata kuliah wajib dipilih.");
        }
        this.mataKuliahId = mataKuliahId.trim();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) throws ValidasiInputException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidasiInputException("Nama komponen penilaian tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public int getBobot() {
        return bobot;
    }

    public void setBobot(int bobot) throws ValidasiInputException {
        if (bobot <= 0 || bobot > 100) {
            throw new ValidasiInputException("Bobot komponen harus antara 1% hingga 100%.");
        }
        this.bobot = bobot;
    }

    public int getKkm() {
        return kkm;
    }

    public void setKkm(int kkm) throws ValidasiInputException {
        if (kkm < 0 || kkm > 100) {
            throw new ValidasiInputException("KKM harus antara 0 hingga 100.");
        }
        this.kkm = kkm;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan == null ? "" : keterangan.trim();
    }

    /**
     * Menghitung bobot efektif komponen (polimorfik per jenis komponen).
     */
    public abstract double hitungBobotEfektif();

    /**
     * Mengembalikan kode jenis komponen: TUGAS, KUIS, atau UJIAN.
     */
    public abstract String getJenisKomponen();

    @Override
    public double hitungKontribusi(double skor) {
        return skor * (hitungBobotEfektif() / 100.0);
    }

    @Override
    public void validasi() throws ValidasiInputException {
        if (nama == null || nama.isEmpty()) {
            throw new ValidasiInputException("Nama komponen wajib diisi.");
        }
        if (bobot <= 0 || bobot > 100) {
            throw new ValidasiInputException("Bobot harus antara 1 - 100.");
        }
    }
}
