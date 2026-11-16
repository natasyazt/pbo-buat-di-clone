package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representasi entitas Mahasiswa.
 * Mewarisi kelas abstrak Person dan mengelola daftar ID mata kuliah yang diambil.
 */
public class Mahasiswa extends Person {

    public enum StatusMahasiswa {
        AKTIF("Aktif"),
        CUTI("Cuti"),
        BERISIKO("Berisiko"),
        NON_AKTIF("Non-Aktif");

        private final String label;

        StatusMahasiswa(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static StatusMahasiswa fromLabel(String text) {
            for (StatusMahasiswa s : values()) {
                if (s.label.equalsIgnoreCase(text) || s.name().equalsIgnoreCase(text)) {
                    return s;
                }
            }
            return AKTIF;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private String nim;
    private String angkatan;
    private String prodi;
    private String kelas;
    private int kehadiranPersen;
    private StatusMahasiswa status;
    private final List<String> mataKuliahIds;

    public Mahasiswa(String id, String nim, String nama, String email, String angkatan,
                     String prodi, String kelas, int kehadiranPersen,
                     StatusMahasiswa status, List<String> mataKuliahIds) throws ValidasiInputException {
        super(id, nama, email);
        setNim(nim);
        setAngkatan(angkatan);
        setProdi(prodi);
        setKelas(kelas);
        setKehadiranPersen(kehadiranPersen);
        setStatus(status);
        this.mataKuliahIds = new ArrayList<>();
        if (mataKuliahIds != null) {
            this.mataKuliahIds.addAll(mataKuliahIds);
        }
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) throws ValidasiInputException {
        if (nim == null || nim.trim().isEmpty()) {
            throw new ValidasiInputException("NIM mahasiswa tidak boleh kosong.");
        }
        this.nim = nim.trim();
    }

    public String getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(String angkatan) throws ValidasiInputException {
        if (angkatan == null || angkatan.trim().isEmpty()) {
            throw new ValidasiInputException("Angkatan mahasiswa wajib diisi.");
        }
        this.angkatan = angkatan.trim();
    }

    public String getProdi() {
        return prodi;
    }

    public void setProdi(String prodi) throws ValidasiInputException {
        if (prodi == null || prodi.trim().isEmpty()) {
            throw new ValidasiInputException("Program studi wajib diisi.");
        }
        this.prodi = prodi.trim();
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) throws ValidasiInputException {
        if (kelas == null || kelas.trim().isEmpty()) {
            throw new ValidasiInputException("Kelas mahasiswa wajib diisi.");
        }
        this.kelas = kelas.trim();
    }

    public int getKehadiranPersen() {
        return kehadiranPersen;
    }

    public void setKehadiranPersen(int kehadiranPersen) throws ValidasiInputException {
        if (kehadiranPersen < 0 || kehadiranPersen > 100) {
            throw new ValidasiInputException("Persentase kehadiran harus antara 0 hingga 100.");
        }
        this.kehadiranPersen = kehadiranPersen;
    }

    public StatusMahasiswa getStatus() {
        return status;
    }

    public void setStatus(StatusMahasiswa status) throws ValidasiInputException {
        if (status == null) {
            throw new ValidasiInputException("Status mahasiswa tidak boleh null.");
        }
        this.status = status;
    }

    public List<String> getMataKuliahIds() {
        return Collections.unmodifiableList(mataKuliahIds);
    }

    public void setMataKuliahIds(List<String> ids) {
        this.mataKuliahIds.clear();
        if (ids != null) {
            this.mataKuliahIds.addAll(ids);
        }
    }

    public boolean mengambilMataKuliah(String mataKuliahId) {
        return mataKuliahIds.contains(mataKuliahId);
    }

    @Override
    public String getPeran() {
        return "Mahasiswa";
    }

    @Override
    public String getRingkasanIdentitas() {
        return nim + " - " + nama + " (" + kelas + ")";
    }

    @Override
    public void validasi() throws ValidasiInputException {
        super.validasi();
        if (nim == null || nim.isEmpty()) {
            throw new ValidasiInputException("NIM wajib diisi.");
        }
        if (kehadiranPersen < 0 || kehadiranPersen > 100) {
            throw new ValidasiInputException("Kehadiran harus 0-100%.");
        }
    }
}
