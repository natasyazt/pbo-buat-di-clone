package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;

/**
 * Subkelas KomponenPenilaian untuk Tugas / Praktikum / Proyek.
 */
public class KomponenTugas extends KomponenPenilaian {
    private boolean tugasPraktikum;

    public KomponenTugas(String id, String mataKuliahId, String nama,
                         int bobot, int kkm, String keterangan,
                         boolean tugasPraktikum) throws ValidasiInputException {
        super(id, mataKuliahId, nama, bobot, kkm, keterangan);
        this.tugasPraktikum = tugasPraktikum;
    }

    public boolean isTugasPraktikum() {
        return tugasPraktikum;
    }

    public void setTugasPraktikum(boolean tugasPraktikum) {
        this.tugasPraktikum = tugasPraktikum;
    }

    @Override
    public double hitungBobotEfektif() {
        // Bobot efektif tugas tetap mengikuti persentase bobot yang ditetapkan
        return bobot;
    }

    @Override
    public double hitungKontribusi(double skor) {
        // Jika komponen praktikum memenuhi KKM, berikan kontribusi standar
        return super.hitungKontribusi(skor);
    }

    @Override
    public String getJenisKomponen() {
        return "TUGAS";
    }

    @Override
    public String getKategoriPenilaian() {
        return tugasPraktikum ? "Tugas & Praktikum" : "Tugas Mandiri";
    }
}
