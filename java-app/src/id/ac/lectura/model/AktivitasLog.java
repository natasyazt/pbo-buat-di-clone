package id.ac.lectura.model;

/**
 * Log aktivitas sistem untuk ditampilkan di Dashboard.
 */
public class AktivitasLog {
    private final String id;
    private final String waktu;
    private final String aksi;
    private final String kategori;
    private final String detail;

    public AktivitasLog(String id, String waktu, String aksi, String kategori, String detail) {
        this.id = id;
        this.waktu = waktu;
        this.aksi = aksi;
        this.kategori = kategori;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getAksi() {
        return aksi;
    }

    public String getKategori() {
        return kategori;
    }

    public String getDetail() {
        return detail;
    }
}
