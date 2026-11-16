package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;

/**
 * Subkelas KomponenPenilaian untuk Kuis / Evaluasi Singkat.
 */
public class KomponenKuis extends KomponenPenilaian {
    private int jumlahSesi;

    public KomponenKuis(String id, String mataKuliahId, String nama,
                        int bobot, int kkm, String keterangan,
                        int jumlahSesi) throws ValidasiInputException {
        super(id, mataKuliahId, nama, bobot, kkm, keterangan);
        setJumlahSesi(jumlahSesi);
    }

    public int getJumlahSesi() {
        return jumlahSesi;
    }

    public void setJumlahSesi(int jumlahSesi) throws ValidasiInputException {
        if (jumlahSesi < 1) {
            throw new ValidasiInputException("Jumlah sesi kuis minimal 1.");
        }
        this.jumlahSesi = jumlahSesi;
    }

    @Override
    public double hitungBobotEfektif() {
        return bobot;
    }

    @Override
    public String getJenisKomponen() {
        return "KUIS";
    }

    @Override
    public String getKategoriPenilaian() {
        return "Kuis (" + jumlahSesi + "x)";
    }
}
