package id.ac.lectura.model;

import java.util.Collections;
import java.util.Map;

/**
 * Hasil kalkulasi rekapitulasi nilai akhir mahasiswa pada satu mata kuliah.
 */
public class RekapNilai {
    private final Mahasiswa mahasiswa;
    private final Map<String, Double> nilaiKomponen;
    private final double nilaiAkhir;
    private final String hurufMutu;
    private final double indeks;
    private final String statusLulus;
    private final String catatanDosen;

    public RekapNilai(Mahasiswa mahasiswa, Map<String, Double> nilaiKomponen,
                      double nilaiAkhir, String hurufMutu, double indeks,
                      String statusLulus, String catatanDosen) {
        this.mahasiswa = mahasiswa;
        this.nilaiKomponen = nilaiKomponen == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(nilaiKomponen);
        this.nilaiAkhir = nilaiAkhir;
        this.hurufMutu = hurufMutu;
        this.indeks = indeks;
        this.statusLulus = statusLulus;
        this.catatanDosen = catatanDosen == null ? "" : catatanDosen;
    }

    public Mahasiswa getMahasiswa() {
        return mahasiswa;
    }

    public Map<String, Double> getNilaiKomponen() {
        return nilaiKomponen;
    }

    public double getNilaiAkhir() {
        return nilaiAkhir;
    }

    public String getHurufMutu() {
        return hurufMutu;
    }

    public double getIndeks() {
        return indeks;
    }

    public String getStatusLulus() {
        return statusLulus;
    }

    public String getCatatanDosen() {
        return catatanDosen;
    }
}
