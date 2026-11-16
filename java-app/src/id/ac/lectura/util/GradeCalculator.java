package id.ac.lectura.util;

import id.ac.lectura.model.KomponenPenilaian;
import id.ac.lectura.model.Mahasiswa;
import id.ac.lectura.model.NilaiMahasiswa;
import id.ac.lectura.model.RekapNilai;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utilitas perhitungan nilai akhir, konversi huruf mutu, indeks, dan status kelulusan.
 * Menerapkan polimorfisme melalui referensi kelas induk KomponenPenilaian
 * serta method overloading pada konversiNilaiHuruf.
 */
public final class GradeCalculator {

    private GradeCalculator() {
    }

    /**
     * Overloading #1: Konversi nilai akhir (0-100) ke huruf mutu standar.
     */
    public static String konversiNilaiHuruf(double nilaiAkhir) {
        double rounded = Math.round(nilaiAkhir * 10.0) / 10.0;
        if (rounded >= 80.0) return "A";
        if (rounded >= 75.0) return "AB";
        if (rounded >= 70.0) return "B";
        if (rounded >= 65.0) return "BC";
        if (rounded >= 60.0) return "C";
        if (rounded >= 50.0) return "D";
        return "E";
    }

    /**
     * Overloading #2: Konversi nilai akhir ke huruf mutu dengan batas KKM khusus.
     * Jika nilai akhir di bawah batas KKM minimal, maksimal huruf mutu adalah D (Remidi).
     */
    public static String konversiNilaiHuruf(double nilaiAkhir, double batasKkm) {
        String huruf = konversiNilaiHuruf(nilaiAkhir);
        if (nilaiAkhir < batasKkm && ("A".equals(huruf) || "AB".equals(huruf)
                || "B".equals(huruf) || "BC".equals(huruf) || "C".equals(huruf))) {
            return "D";
        }
        return huruf;
    }

    public static double hitungIndeks(String hurufMutu) {
        switch (hurufMutu) {
            case "A":  return 4.0;
            case "AB": return 3.5;
            case "B":  return 3.0;
            case "BC": return 2.5;
            case "C":  return 2.0;
            case "D":  return 1.0;
            default:   return 0.0;
        }
    }

    public static String tentukanStatus(String hurufMutu) {
        if ("D".equals(hurufMutu)) {
            return "REMIDI";
        }
        if ("E".equals(hurufMutu)) {
            return "TIDAK LULUS";
        }
        return "LULUS";
    }

    /**
     * Menghitung rekap nilai mahasiswa menggunakan pemanggilan polimorfik
     * pada daftar KomponenPenilaian.
     */
    public static RekapNilai hitungRekap(Mahasiswa mahasiswa,
                                         List<KomponenPenilaian> komponenList,
                                         NilaiMahasiswa nilaiRecord) {
        Map<String, Double> scores = nilaiRecord != null
                ? nilaiRecord.getNilaiKomponenMap()
                : Collections.emptyMap();

        double totalBobotEfektif = 0.0;
        double totalNilaiTertimbang = 0.0;

        for (KomponenPenilaian komp : komponenList) {
            double bobotEfektif = komp.hitungBobotEfektif();
            totalBobotEfektif += bobotEfektif;
            double skor = scores.getOrDefault(komp.getId(), 0.0);
            // Pemanggilan polimorfik hitungKontribusi(skor)
            totalNilaiTertimbang += komp.hitungKontribusi(skor);
        }

        double nilaiAkhir = totalBobotEfektif > 0.0
                ? (totalNilaiTertimbang / (totalBobotEfektif / 100.0))
                : 0.0;

        double roundedAkhir = Math.round(nilaiAkhir * 100.0) / 100.0;
        String huruf = konversiNilaiHuruf(roundedAkhir);
        double indeks = hitungIndeks(huruf);
        String status = tentukanStatus(huruf);
        String catatan = nilaiRecord != null ? nilaiRecord.getCatatanDosen() : "";

        return new RekapNilai(mahasiswa, scores, roundedAkhir, huruf, indeks, status, catatan);
    }
}
