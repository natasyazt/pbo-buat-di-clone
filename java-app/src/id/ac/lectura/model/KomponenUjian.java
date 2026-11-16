package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;

/**
 * Subkelas KomponenPenilaian untuk Ujian (UTS, UAS, Proyek Akhir).
 */
public class KomponenUjian extends KomponenPenilaian {
    private String tipeUjian;

    public KomponenUjian(String id, String mataKuliahId, String nama,
                         int bobot, int kkm, String keterangan,
                         String tipeUjian) throws ValidasiInputException {
        super(id, mataKuliahId, nama, bobot, kkm, keterangan);
        setTipeUjian(tipeUjian);
    }

    public String getTipeUjian() {
        return tipeUjian;
    }

    public void setTipeUjian(String tipeUjian) {
        this.tipeUjian = (tipeUjian == null || tipeUjian.trim().isEmpty())
                ? "Ujian Tertulis"
                : tipeUjian.trim();
    }

    @Override
    public double hitungBobotEfektif() {
        return bobot;
    }

    @Override
    public double hitungKontribusi(double skor) {
        return super.hitungKontribusi(skor);
    }

    @Override
    public String getJenisKomponen() {
        return "UJIAN";
    }

    @Override
    public String getKategoriPenilaian() {
        return "Ujian (" + tipeUjian + ")";
    }
}
