package id.ac.lectura.controller;

import id.ac.lectura.dao.AcademicRepository;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.AktivitasLog;
import id.ac.lectura.model.KomponenPenilaian;
import id.ac.lectura.model.Mahasiswa;
import id.ac.lectura.model.MataKuliah;
import id.ac.lectura.model.NilaiMahasiswa;
import id.ac.lectura.model.RekapNilai;
import id.ac.lectura.util.GradeCalculator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller utama (MVC) yang memisahkan logika bisnis akademik dan persistensi
 * dari lapisan antarmuka grafis (View).
 */
public class AcademicController {
    private final AcademicRepository repository;
    private final List<MataKuliah> mataKuliahList = new ArrayList<>();
    private final List<Mahasiswa> mahasiswaList = new ArrayList<>();
    private final List<KomponenPenilaian> komponenList = new ArrayList<>();
    private final List<NilaiMahasiswa> nilaiList = new ArrayList<>();
    private final List<AktivitasLog> aktivitasList = new ArrayList<>();
    private final List<Runnable> changeListeners = new ArrayList<>();

    private String selectedMataKuliahId;

    public AcademicController(AcademicRepository repository) {
        this.repository = repository;
    }

    public void addChangeListener(Runnable listener) {
        if (listener != null && !changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }

    private void notifyListeners() {
        for (Runnable r : changeListeners) {
            r.run();
        }
    }

    public void initializeData() throws DataPersistenceException, ValidasiInputException {
        mataKuliahList.clear();
        mahasiswaList.clear();
        komponenList.clear();
        nilaiList.clear();
        aktivitasList.clear();

        if (repository.hasPersistedData()) {
            mataKuliahList.addAll(repository.loadMataKuliah());
            mahasiswaList.addAll(repository.loadMahasiswa());
            komponenList.addAll(repository.loadKomponen());
            nilaiList.addAll(repository.loadNilai());
            aktivitasList.addAll(repository.loadAktivitas());
        } else {
            loadInitialData();
            saveAll();
        }

        if (!mataKuliahList.isEmpty()) {
            selectedMataKuliahId = mataKuliahList.get(0).getId();
        }
        notifyListeners();
    }

    private void loadInitialData() throws ValidasiInputException {
        mataKuliahList.addAll(repository.createDemoMataKuliah());
        komponenList.addAll(repository.createDemoKomponen());
        mahasiswaList.addAll(repository.createDemoMahasiswa());
        nilaiList.addAll(repository.createDemoNilai());
        aktivitasList.addAll(repository.createDemoAktivitas());
    }

    public void saveAll() throws DataPersistenceException {
        repository.saveAll(mataKuliahList, mahasiswaList, komponenList, nilaiList, aktivitasList);
    }

    public String getSelectedMataKuliahId() {
        return selectedMataKuliahId;
    }

    public void setSelectedMataKuliahId(String selectedMataKuliahId) {
        this.selectedMataKuliahId = selectedMataKuliahId;
        notifyListeners();
    }

    public MataKuliah getSelectedMataKuliah() {
        for (MataKuliah mk : mataKuliahList) {
            if (mk.getId().equals(selectedMataKuliahId)) {
                return mk;
            }
        }
        return mataKuliahList.isEmpty() ? null : mataKuliahList.get(0);
    }

    // --- Mata Kuliah CRUD ---
    public List<MataKuliah> getMataKuliahList() {
        return Collections.unmodifiableList(mataKuliahList);
    }

    public void tambahMataKuliah(MataKuliah mk) throws ValidasiInputException, DataPersistenceException {
        mk.validasi();
        mataKuliahList.add(mk);
        if (selectedMataKuliahId == null) {
            selectedMataKuliahId = mk.getId();
        }
        catatAktivitas("Tambah Mata Kuliah", "MATA_KULIAH", mk.getKode() + " - " + mk.getNama());
        saveAll();
        notifyListeners();
    }

    public void ubahMataKuliah(MataKuliah updated) throws ValidasiInputException, DataPersistenceException {
        updated.validasi();
        for (int i = 0; i < mataKuliahList.size(); i++) {
            if (mataKuliahList.get(i).getId().equals(updated.getId())) {
                mataKuliahList.set(i, updated);
                catatAktivitas("Ubah Mata Kuliah", "MATA_KULIAH", updated.getKode() + " - " + updated.getNama());
                saveAll();
                notifyListeners();
                return;
            }
        }
        throw new ValidasiInputException("Mata kuliah tidak ditemukan.");
    }

    public void hapusMataKuliah(String id) throws DataPersistenceException {
        MataKuliah target = null;
        for (MataKuliah mk : mataKuliahList) {
            if (mk.getId().equals(id)) {
                target = mk;
                break;
            }
        }
        if (target != null) {
            mataKuliahList.remove(target);
            komponenList.removeIf(k -> k.getMataKuliahId().equals(id));
            nilaiList.removeIf(n -> n.getMataKuliahId().equals(id));
            if (id.equals(selectedMataKuliahId)) {
                selectedMataKuliahId = mataKuliahList.isEmpty() ? null : mataKuliahList.get(0).getId();
            }
            catatAktivitas("Hapus Mata Kuliah", "MATA_KULIAH", target.getKode() + " - " + target.getNama());
            saveAll();
            notifyListeners();
        }
    }

    // --- Mahasiswa CRUD + Overloaded Search ---
    public List<Mahasiswa> getMahasiswaList() {
        return Collections.unmodifiableList(mahasiswaList);
    }

    /**
     * Overloading #1: Cari mahasiswa berdasarkan kata kunci (NIM, Nama, atau Kelas).
     */
    public List<Mahasiswa> cariMahasiswa(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Mahasiswa> result = new ArrayList<>();
        for (Mahasiswa m : mahasiswaList) {
            if (q.isEmpty()
                    || m.getNim().toLowerCase().contains(q)
                    || m.getNama().toLowerCase().contains(q)
                    || m.getKelas().toLowerCase().contains(q)) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Overloading #2: Cari mahasiswa berdasarkan kata kunci dan filter status.
     */
    public List<Mahasiswa> cariMahasiswa(String keyword, Mahasiswa.StatusMahasiswa statusFilter) {
        List<Mahasiswa> base = cariMahasiswa(keyword);
        if (statusFilter == null) {
            return base;
        }
        List<Mahasiswa> filtered = new ArrayList<>();
        for (Mahasiswa m : base) {
            if (m.getStatus() == statusFilter) {
                filtered.add(m);
            }
        }
        return filtered;
    }

    public void tambahMahasiswa(Mahasiswa mhs) throws ValidasiInputException, DataPersistenceException {
        mhs.validasi();
        for (Mahasiswa existing : mahasiswaList) {
            if (existing.getNim().equalsIgnoreCase(mhs.getNim())) {
                throw new ValidasiInputException("NIM " + mhs.getNim() + " sudah terdaftar.");
            }
        }
        mahasiswaList.add(mhs);
        catatAktivitas("Tambah Mahasiswa", "MAHASISWA", mhs.getNim() + " - " + mhs.getNama());
        saveAll();
        notifyListeners();
    }

    public void ubahMahasiswa(Mahasiswa updated) throws ValidasiInputException, DataPersistenceException {
        updated.validasi();
        for (int i = 0; i < mahasiswaList.size(); i++) {
            if (mahasiswaList.get(i).getId().equals(updated.getId())) {
                mahasiswaList.set(i, updated);
                catatAktivitas("Ubah Mahasiswa", "MAHASISWA", updated.getNim() + " - " + updated.getNama());
                saveAll();
                notifyListeners();
                return;
            }
        }
        throw new ValidasiInputException("Data mahasiswa tidak ditemukan.");
    }

    public void hapusMahasiswa(String id) throws DataPersistenceException {
        Mahasiswa target = null;
        for (Mahasiswa m : mahasiswaList) {
            if (m.getId().equals(id)) {
                target = m;
                break;
            }
        }
        if (target != null) {
            mahasiswaList.remove(target);
            nilaiList.removeIf(n -> n.getMahasiswaId().equals(id));
            catatAktivitas("Hapus Mahasiswa", "MAHASISWA", target.getNim() + " - " + target.getNama());
            saveAll();
            notifyListeners();
        }
    }

    // --- Komponen Penilaian CRUD ---
    public List<KomponenPenilaian> getKomponenByMataKuliah(String mkId) {
        List<KomponenPenilaian> result = new ArrayList<>();
        if (mkId == null) return result;
        for (KomponenPenilaian k : komponenList) {
            if (mkId.equals(k.getMataKuliahId())) {
                result.add(k);
            }
        }
        return result;
    }

    public int getTotalBobotMataKuliah(String mkId) {
        int total = 0;
        for (KomponenPenilaian k : getKomponenByMataKuliah(mkId)) {
            total += k.getBobot();
        }
        return total;
    }

    public void tambahKomponen(KomponenPenilaian komp) throws ValidasiInputException, DataPersistenceException {
        komp.validasi();
        komponenList.add(komp);
        catatAktivitas("Tambah Komponen", "KOMPONEN", komp.getNama() + " (" + komp.getBobot() + "%)");
        saveAll();
        notifyListeners();
    }

    public void ubahKomponen(KomponenPenilaian updated) throws ValidasiInputException, DataPersistenceException {
        updated.validasi();
        for (int i = 0; i < komponenList.size(); i++) {
            if (komponenList.get(i).getId().equals(updated.getId())) {
                komponenList.set(i, updated);
                catatAktivitas("Ubah Komponen", "KOMPONEN", updated.getNama() + " (" + updated.getBobot() + "%)");
                saveAll();
                notifyListeners();
                return;
            }
        }
        throw new ValidasiInputException("Komponen penilaian tidak ditemukan.");
    }

    public void hapusKomponen(String id) throws DataPersistenceException {
        KomponenPenilaian target = null;
        for (KomponenPenilaian k : komponenList) {
            if (k.getId().equals(id)) {
                target = k;
                break;
            }
        }
        if (target != null) {
            komponenList.remove(target);
            catatAktivitas("Hapus Komponen", "KOMPONEN", target.getNama());
            saveAll();
            notifyListeners();
        }
    }

    // --- Nilai & Rekap ---
    public NilaiMahasiswa findNilaiRecord(String mahasiswaId, String mkId) {
        for (NilaiMahasiswa n : nilaiList) {
            if (n.getMahasiswaId().equals(mahasiswaId) && n.getMataKuliahId().equals(mkId)) {
                return n;
            }
        }
        return null;
    }

    public void simpanNilaiMahasiswa(String mahasiswaId, String mkId,
                                     Map<String, Double> skorMap, String catatanDosen)
            throws ValidasiInputException, DataPersistenceException {
        NilaiMahasiswa existing = findNilaiRecord(mahasiswaId, mkId);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        if (existing == null) {
            existing = new NilaiMahasiswa(
                    "n-" + UUID.randomUUID().toString().substring(0, 8),
                    mahasiswaId,
                    mkId,
                    skorMap,
                    catatanDosen,
                    now
            );
            nilaiList.add(existing);
        } else {
            for (Map.Entry<String, Double> e : skorMap.entrySet()) {
                existing.setNilaiKomponen(e.getKey(), e.getValue());
            }
            existing.setCatatanDosen(catatanDosen);
            existing.setTerakhirDiubah(now);
        }
        existing.validasi();
        catatAktivitas("Simpan Nilai", "NILAI", "Memperbarui nilai mahasiswa ID " + mahasiswaId);
        saveAll();
        notifyListeners();
    }

    public List<RekapNilai> getRekapByMataKuliah(String mkId) {
        List<RekapNilai> rekapList = new ArrayList<>();
        if (mkId == null) return rekapList;

        List<KomponenPenilaian> kompMk = getKomponenByMataKuliah(mkId);
        for (Mahasiswa m : mahasiswaList) {
            if (m.mengambilMataKuliah(mkId)) {
                NilaiMahasiswa record = findNilaiRecord(m.getId(), mkId);
                rekapList.add(GradeCalculator.hitungRekap(m, kompMk, record));
            }
        }
        return rekapList;
    }

    public Map<String, Integer> getDistribusiHurufMutu(String mkId) {
        Map<String, Integer> dist = new LinkedHashMap<>();
        String[] grades = {"A", "AB", "B", "BC", "C", "D", "E"};
        for (String g : grades) {
            dist.put(g, 0);
        }
        for (RekapNilai r : getRekapByMataKuliah(mkId)) {
            dist.put(r.getHurufMutu(), dist.getOrDefault(r.getHurufMutu(), 0) + 1);
        }
        return dist;
    }

    public List<AktivitasLog> getAktivitasList() {
        return Collections.unmodifiableList(aktivitasList);
    }

    public void logAktivitas(String aksi, String kategori, String detail) {
        catatAktivitas(aksi, kategori, detail);
        try {
            saveAll();
        } catch (DataPersistenceException ignored) {
            // Aktivitas tetap tercatat di memori jika penyimpanan sementara gagal
        }
        notifyListeners();
    }

    private void catatAktivitas(String aksi, String kategori, String detail) {
        String waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        aktivitasList.add(0, new AktivitasLog(
                "act-" + UUID.randomUUID().toString().substring(0, 6),
                waktu, aksi, kategori, detail
        ));
        if (aktivitasList.size() > 30) {
            aktivitasList.remove(aktivitasList.size() - 1);
        }
    }
}
