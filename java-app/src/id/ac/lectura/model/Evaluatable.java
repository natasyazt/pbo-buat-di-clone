package id.ac.lectura.model;

/**
 * Interface untuk komponen atau entitas yang dapat dievaluasi kontribusi nilainya.
 */
public interface Evaluatable {
    double hitungKontribusi(double skor);

    String getKategoriPenilaian();
}
