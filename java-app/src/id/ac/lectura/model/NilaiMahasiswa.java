package id.ac.lectura.model;

import id.ac.lectura.exception.ValidasiInputException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Menyimpan nilai per komponen untuk satu mahasiswa pada satu mata kuliah.
 * Menggunakan Map<String, Double> generik.
 */
public class NilaiMahasiswa implements Validatable {
    private String id;
    private String mahasiswaId;
    private String mataKuliahId;
    private final Map<String, Double> nilaiKomponen;
    private String catatanDosen;
    private String terakhirDiubah;

    public NilaiMahasiswa(String id, String mahasiswaId, String mataKuliahId,
                          Map<String, Double> nilaiKomponen, String catatanDosen,
                          String terakhirDiubah) throws ValidasiInputException {
        setId(id);
        setMahasiswaId(mahasiswaId);
        setMataKuliahId(mataKuliahId);
        this.nilaiKomponen = new LinkedHashMap<>();
        if (nilaiKomponen != null) {
            for (Map.Entry<String, Double> entry : nilaiKomponen.entrySet()) {
                setNilaiKomponen(entry.getKey(), entry.getValue());
            }
        }
        setCatatanDosen(catatanDosen);
        setTerakhirDiubah(terakhirDiubah);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidasiInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidasiInputException("ID nilai tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    public String getMahasiswaId() {
        return mahasiswaId;
    }

    public void setMahasiswaId(String mahasiswaId) throws ValidasiInputException {
        if (mahasiswaId == null || mahasiswaId.trim().isEmpty()) {
            throw new ValidasiInputException("ID mahasiswa wajib diisi.");
        }
        this.mahasiswaId = mahasiswaId.trim();
    }

    public String getMataKuliahId() {
        return mataKuliahId;
    }

    public void setMataKuliahId(String mataKuliahId) throws ValidasiInputException {
        if (mataKuliahId == null || mataKuliahId.trim().isEmpty()) {
            throw new ValidasiInputException("ID mata kuliah wajib diisi.");
        }
        this.mataKuliahId = mataKuliahId.trim();
    }

    public Map<String, Double> getNilaiKomponenMap() {
        return Collections.unmodifiableMap(nilaiKomponen);
    }

    public double getNilaiKomponen(String komponenId) {
        return nilaiKomponen.getOrDefault(komponenId, 0.0);
    }

    public void setNilaiKomponen(String komponenId, double nilai) throws ValidasiInputException {
        if (komponenId == null || komponenId.trim().isEmpty()) {
            throw new ValidasiInputException("ID komponen tidak valid.");
        }
        if (nilai < 0.0 || nilai > 100.0) {
            throw new ValidasiInputException("Nilai harus berada pada rentang 0 sampai 100.");
        }
        nilaiKomponen.put(komponenId.trim(), nilai);
    }

    public String getCatatanDosen() {
        return catatanDosen;
    }

    public void setCatatanDosen(String catatanDosen) {
        this.catatanDosen = catatanDosen == null ? "" : catatanDosen.trim();
    }

    public String getTerakhirDiubah() {
        return terakhirDiubah;
    }

    public void setTerakhirDiubah(String terakhirDiubah) {
        this.terakhirDiubah = terakhirDiubah == null ? "" : terakhirDiubah.trim();
    }

    @Override
    public void validasi() throws ValidasiInputException {
        for (Map.Entry<String, Double> e : nilaiKomponen.entrySet()) {
            if (e.getValue() < 0.0 || e.getValue() > 100.0) {
                throw new ValidasiInputException("Nilai komponen " + e.getKey() + " tidak valid: " + e.getValue());
            }
        }
    }
}
