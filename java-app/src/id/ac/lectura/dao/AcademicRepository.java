package id.ac.lectura.dao;

import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.AktivitasLog;
import id.ac.lectura.model.Dosen;
import id.ac.lectura.model.KomponenKuis;
import id.ac.lectura.model.KomponenPenilaian;
import id.ac.lectura.model.KomponenTugas;
import id.ac.lectura.model.KomponenUjian;
import id.ac.lectura.model.Mahasiswa;
import id.ac.lectura.model.MataKuliah;
import id.ac.lectura.model.NilaiMahasiswa;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lapisan DAO (Data Access Object) untuk membaca dan menyimpan data akademik
 * secara permanen ke berkas teks terstruktur (TSV) di folder data/.
 */
public class AcademicRepository {
    private static final String DELIM = "\t";
    private final Path dataDir;

    public AcademicRepository(String dataDirPath) {
        this.dataDir = Paths.get(dataDirPath);
    }

    public Dosen getDefaultDosen() {
        try {
            return new Dosen(
                    "dos-001",
                    "19820412 200812 2 001",
                    "Dra. Rina Puspita, M.Kom.",
                    "Dosen Tetap Informatika",
                    "rina.puspita@sipena.ac.id",
                    "S1 Teknik Komputer",
                    "Fakultas Informatika & Rekayasa Komputer",
                    "password123"
            );
        } catch (ValidasiInputException e) {
            throw new IllegalStateException("Data dosen bawaan tidak valid", e);
        }
    }

    public boolean hasPersistedData() {
        return Files.exists(dataDir.resolve("matakuliah.tsv"))
                && Files.exists(dataDir.resolve("mahasiswa.tsv"));
    }

    private void ensureDir() throws DataPersistenceException {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Gagal membuat direktori penyimpanan: " + dataDir, e);
        }
    }

    private String clean(String value) {
        if (value == null) return "";
        return value.replace("\t", " ").replace("\r", " ").replace("\n", " ");
    }

    public void saveAll(List<MataKuliah> mkList,
                        List<Mahasiswa> mhsList,
                        List<KomponenPenilaian> kompList,
                        List<NilaiMahasiswa> nilaiList,
                        List<AktivitasLog> logList) throws DataPersistenceException {
        ensureDir();
        saveMataKuliah(mkList);
        saveMahasiswa(mhsList);
        saveKomponen(kompList);
        saveNilai(nilaiList);
        saveAktivitas(logList);
    }

    private void saveMataKuliah(List<MataKuliah> list) throws DataPersistenceException {
        Path file = dataDir.resolve("matakuliah.tsv");
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (MataKuliah mk : list) {
                w.write(String.join(DELIM,
                        clean(mk.getId()),
                        clean(mk.getKode()),
                        clean(mk.getNama()),
                        String.valueOf(mk.getSks()),
                        clean(mk.getSemester()),
                        clean(mk.getKelas()),
                        clean(mk.getRuang()),
                        clean(mk.getJadwal()),
                        clean(mk.getDeskripsi())
                ));
                w.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Gagal menyimpan data Mata Kuliah.", e);
        }
    }

    private void saveMahasiswa(List<Mahasiswa> list) throws DataPersistenceException {
        Path file = dataDir.resolve("mahasiswa.tsv");
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (Mahasiswa m : list) {
                String mkIds = String.join(",", m.getMataKuliahIds());
                w.write(String.join(DELIM,
                        clean(m.getId()),
                        clean(m.getNim()),
                        clean(m.getNama()),
                        clean(m.getEmail()),
                        clean(m.getAngkatan()),
                        clean(m.getProdi()),
                        clean(m.getKelas()),
                        String.valueOf(m.getKehadiranPersen()),
                        m.getStatus().name(),
                        clean(mkIds)
                ));
                w.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Gagal menyimpan data Mahasiswa.", e);
        }
    }

    private void saveKomponen(List<KomponenPenilaian> list) throws DataPersistenceException {
        Path file = dataDir.resolve("komponen.tsv");
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (KomponenPenilaian k : list) {
                String extra = "";
                if (k instanceof KomponenTugas) {
                    extra = String.valueOf(((KomponenTugas) k).isTugasPraktikum());
                } else if (k instanceof KomponenKuis) {
                    extra = String.valueOf(((KomponenKuis) k).getJumlahSesi());
                } else if (k instanceof KomponenUjian) {
                    extra = clean(((KomponenUjian) k).getTipeUjian());
                }
                w.write(String.join(DELIM,
                        clean(k.getId()),
                        clean(k.getMataKuliahId()),
                        clean(k.getJenisKomponen()),
                        clean(k.getNama()),
                        String.valueOf(k.getBobot()),
                        String.valueOf(k.getKkm()),
                        clean(k.getKeterangan()),
                        extra
                ));
                w.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Gagal menyimpan data Komponen Penilaian.", e);
        }
    }

    private void saveNilai(List<NilaiMahasiswa> list) throws DataPersistenceException {
        Path file = dataDir.resolve("nilai.tsv");
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (NilaiMahasiswa n : list) {
                StringBuilder mapStr = new StringBuilder();
                for (Map.Entry<String, Double> entry : n.getNilaiKomponenMap().entrySet()) {
                    if (mapStr.length() > 0) mapStr.append(";");
                    mapStr.append(entry.getKey()).append("=").append(entry.getValue());
                }
                w.write(String.join(DELIM,
                        clean(n.getId()),
                        clean(n.getMahasiswaId()),
                        clean(n.getMataKuliahId()),
                        mapStr.toString(),
                        clean(n.getCatatanDosen()),
                        clean(n.getTerakhirDiubah())
                ));
                w.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Gagal menyimpan data Nilai Mahasiswa.", e);
        }
    }

    private void saveAktivitas(List<AktivitasLog> list) throws DataPersistenceException {
        Path file = dataDir.resolve("aktivitas.tsv");
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (AktivitasLog a : list) {
                w.write(String.join(DELIM,
                        clean(a.getId()),
                        clean(a.getWaktu()),
                        clean(a.getAksi()),
                        clean(a.getKategori()),
                        clean(a.getDetail())
                ));
                w.newLine();
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Gagal menyimpan log aktivitas.", e);
        }
    }

    public List<MataKuliah> loadMataKuliah() throws DataPersistenceException {
        List<MataKuliah> result = new ArrayList<>();
        Path file = dataDir.resolve("matakuliah.tsv");
        if (!Files.exists(file)) return result;

        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(DELIM, -1);
                if (p.length >= 9) {
                    result.add(new MataKuliah(
                            p[0], p[1], p[2], Integer.parseInt(p[3]),
                            p[4], p[5], p[6], p[7], p[8]
                    ));
                }
            }
        } catch (IOException | NumberFormatException | ValidasiInputException e) {
            throw new DataPersistenceException("Gagal memuat data Mata Kuliah.", e);
        }
        return result;
    }

    public List<Mahasiswa> loadMahasiswa() throws DataPersistenceException {
        List<Mahasiswa> result = new ArrayList<>();
        Path file = dataDir.resolve("mahasiswa.tsv");
        if (!Files.exists(file)) return result;

        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(DELIM, -1);
                if (p.length >= 10) {
                    List<String> mkIds = new ArrayList<>();
                    if (!p[9].trim().isEmpty()) {
                        mkIds.addAll(Arrays.asList(p[9].split(",")));
                    }
                    result.add(new Mahasiswa(
                            p[0], p[1], p[2], p[3], p[4], p[5], p[6],
                            Integer.parseInt(p[7]),
                            Mahasiswa.StatusMahasiswa.fromLabel(p[8]),
                            mkIds
                    ));
                }
            }
        } catch (IOException | NumberFormatException | ValidasiInputException e) {
            throw new DataPersistenceException("Gagal memuat data Mahasiswa.", e);
        }
        return result;
    }

    public List<KomponenPenilaian> loadKomponen() throws DataPersistenceException {
        List<KomponenPenilaian> result = new ArrayList<>();
        Path file = dataDir.resolve("komponen.tsv");
        if (!Files.exists(file)) return result;

        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(DELIM, -1);
                if (p.length >= 8) {
                    String id = p[0];
                    String mkId = p[1];
                    String jenis = p[2];
                    String nama = p[3];
                    int bobot = Integer.parseInt(p[4]);
                    int kkm = Integer.parseInt(p[5]);
                    String ket = p[6];
                    String extra = p[7];

                    if ("TUGAS".equalsIgnoreCase(jenis)) {
                        result.add(new KomponenTugas(id, mkId, nama, bobot, kkm, ket, Boolean.parseBoolean(extra)));
                    } else if ("KUIS".equalsIgnoreCase(jenis)) {
                        int sesi = extra.isEmpty() ? 1 : Integer.parseInt(extra);
                        result.add(new KomponenKuis(id, mkId, nama, bobot, kkm, ket, sesi));
                    } else {
                        result.add(new KomponenUjian(id, mkId, nama, bobot, kkm, ket, extra));
                    }
                }
            }
        } catch (IOException | NumberFormatException | ValidasiInputException e) {
            throw new DataPersistenceException("Gagal memuat data Komponen Penilaian.", e);
        }
        return result;
    }

    public List<NilaiMahasiswa> loadNilai() throws DataPersistenceException {
        List<NilaiMahasiswa> result = new ArrayList<>();
        Path file = dataDir.resolve("nilai.tsv");
        if (!Files.exists(file)) return result;

        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(DELIM, -1);
                if (p.length >= 6) {
                    Map<String, Double> map = new LinkedHashMap<>();
                    if (!p[3].trim().isEmpty()) {
                        for (String pair : p[3].split(";")) {
                            String[] kv = pair.split("=");
                            if (kv.length == 2) {
                                map.put(kv[0], Double.parseDouble(kv[1]));
                            }
                        }
                    }
                    result.add(new NilaiMahasiswa(p[0], p[1], p[2], map, p[4], p[5]));
                }
            }
        } catch (IOException | NumberFormatException | ValidasiInputException e) {
            throw new DataPersistenceException("Gagal memuat data Nilai Mahasiswa.", e);
        }
        return result;
    }

    public List<AktivitasLog> loadAktivitas() throws DataPersistenceException {
        List<AktivitasLog> result = new ArrayList<>();
        Path file = dataDir.resolve("aktivitas.tsv");
        if (!Files.exists(file)) return result;

        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(DELIM, -1);
                if (p.length >= 5) {
                    result.add(new AktivitasLog(p[0], p[1], p[2], p[3], p[4]));
                }
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Gagal memuat log aktivitas.", e);
        }
        return result;
    }

    public List<MataKuliah> createDemoMataKuliah() throws ValidasiInputException {
        List<MataKuliah> list = new ArrayList<>();
        list.add(new MataKuliah("mk-pbo-01", "IF2104", "Pemrograman Berorientasi Objek", 3,
                "Genap 2024/2025", "IF-45-01", "Lab Riset 302", "Senin, 08:00 - 10:30 WIB",
                "Prinsip OOP: Encapsulation, Inheritance, Polymorphism, Abstraction, Design Patterns, dan GUI."));
        list.add(new MataKuliah("mk-pbo-02", "IF2104", "Pemrograman Berorientasi Objek", 3,
                "Genap 2024/2025", "IF-45-02", "Lab Multimedia 204", "Rabu, 13:00 - 15:30 WIB",
                "Prinsip OOP: Encapsulation, Inheritance, Polymorphism, Abstraction, Design Patterns, dan GUI."));
        list.add(new MataKuliah("mk-sda-01", "IF3102", "Struktur Data & Algoritma", 4,
                "Genap 2024/2025", "IF-44-03", "GKB 201", "Selasa, 10:00 - 12:30 WIB",
                "Struktur data linear & non-linear, Tree, Graph, Sorting, Searching, dan Big-O."));
        list.add(new MataKuliah("mk-rpl-01", "IF3205", "Rekayasa Perangkat Lunak", 3,
                "Genap 2024/2025", "IF-44-01", "Gedung FTI R.304", "Kamis, 08:00 - 10:30 WIB",
                "SDLC, Agile Scrum, Software Architecture, UML modeling, Testing, dan Project Management."));
        return list;
    }

    public List<KomponenPenilaian> createDemoKomponen() throws ValidasiInputException {
        List<KomponenPenilaian> list = new ArrayList<>();
        // IF2104 IF-45-01
        list.add(new KomponenKuis("k-pbo-tugas", "mk-pbo-01", "Tugas Mandiri & Kuis", 15, 60,
                "Tugas mingguan latihan class & kuis OOP", 3));
        list.add(new KomponenTugas("k-pbo-lab", "mk-pbo-01", "Praktikum & Modul PBO", 25, 65,
                "Pengerjaan modul praktikum mingguan dan live coding", true));
        list.add(new KomponenUjian("k-pbo-uts", "mk-pbo-01", "Ujian Tengah Semester (UTS)", 25, 60,
                "Ujian teori & implementasi konsep OOP", "UTS"));
        list.add(new KomponenTugas("k-pbo-proyek", "mk-pbo-01", "Proyek Akhir Aplikasi", 20, 65,
                "Pembuatan aplikasi GUI utuh berbasis OOP berkelompok", true));
        list.add(new KomponenUjian("k-pbo-uas", "mk-pbo-01", "Ujian Akhir Semester (UAS)", 15, 60,
                "Evaluasi komprehensif semester akhir", "UAS"));

        // IF2104 IF-45-02
        list.add(new KomponenKuis("k-pbo2-tugas", "mk-pbo-02", "Tugas Mandiri & Kuis", 20, 60,
                "Latihan konsep PBO", 2));
        list.add(new KomponenTugas("k-pbo2-lab", "mk-pbo-02", "Praktikum PBO", 25, 65,
                "Praktikum lab mingguan", true));
        list.add(new KomponenUjian("k-pbo2-uts", "mk-pbo-02", "UTS", 25, 60, "Ujian tengah semester", "UTS"));
        list.add(new KomponenUjian("k-pbo2-uas", "mk-pbo-02", "UAS & Proyek", 30, 60, "Ujian akhir dan demo proyek", "UAS"));

        // IF3102
        list.add(new KomponenTugas("k-sda-tugas", "mk-sda-01", "Tugas Algoritma", 20, 60, "Analisis kompleksitas", false));
        list.add(new KomponenTugas("k-sda-praktikum", "mk-sda-01", "Praktikum Struktur Data", 25, 65, "Implementasi Tree & Graph", true));
        list.add(new KomponenUjian("k-sda-uts", "mk-sda-01", "UTS", 25, 60, "Ujian Tengah Semester", "UTS"));
        list.add(new KomponenUjian("k-sda-uas", "mk-sda-01", "UAS", 30, 60, "Ujian Akhir Semester", "UAS"));

        // IF3205
        list.add(new KomponenTugas("k-rpl-tugas", "mk-rpl-01", "Tugas Analisis SRS", 20, 60, "Dokumen spesifikasi kebutuhan", false));
        list.add(new KomponenTugas("k-rpl-proyek", "mk-rpl-01", "Proyek Perangkat Lunak", 35, 65, "Pengembangan tim Agile", true));
        list.add(new KomponenUjian("k-rpl-uts", "mk-rpl-01", "UTS", 20, 60, "Ujian Tengah Semester", "UTS"));
        list.add(new KomponenUjian("k-rpl-uas", "mk-rpl-01", "UAS Presentasi", 25, 60, "Presentasi akhir sistem", "UAS"));
        return list;
    }

    public List<Mahasiswa> createDemoMahasiswa() throws ValidasiInputException {
        List<Mahasiswa> list = new ArrayList<>();
        list.add(new Mahasiswa("mhs-01", "1301213001", "Muhammad Rayhan Pratama", "rayhan.p@student.ac.id",
                "2023", "Teknik Komputer", "IF-45-01", 94, Mahasiswa.StatusMahasiswa.AKTIF,
                Arrays.asList("mk-pbo-01", "mk-sda-01")));
        list.add(new Mahasiswa("mhs-02", "1301213002", "Siti Nurhaliza Putri", "siti.nurhaliza@student.ac.id",
                "2023", "Teknik Komputer", "IF-45-01", 98, Mahasiswa.StatusMahasiswa.AKTIF,
                Arrays.asList("mk-pbo-01")));
        list.add(new Mahasiswa("mhs-03", "1301213003", "Budi Santoso", "budi.santoso@student.ac.id",
                "2023", "Teknik Informatika", "IF-45-01", 85, Mahasiswa.StatusMahasiswa.AKTIF,
                Arrays.asList("mk-pbo-01", "mk-sda-01")));
        list.add(new Mahasiswa("mhs-04", "1301213004", "Anisa Rahmawati", "anisa.rahma@student.ac.id",
                "2023", "Teknik Informatika", "IF-45-01", 91, Mahasiswa.StatusMahasiswa.AKTIF,
                Arrays.asList("mk-pbo-01")));
        list.add(new Mahasiswa("mhs-05", "1301213005", "Kevin Wijaya Kusuma", "kevin.wijaya@student.ac.id",
                "2023", "Teknik Informatika", "IF-45-01", 70, Mahasiswa.StatusMahasiswa.BERISIKO,
                Arrays.asList("mk-pbo-01")));
        list.add(new Mahasiswa("mhs-06", "1301213006", "Dinda Ayu Maharani", "dinda.maharani@student.ac.id",
                "2023", "Teknik Informatika", "IF-45-01", 96, Mahasiswa.StatusMahasiswa.AKTIF,
                Arrays.asList("mk-pbo-01")));
        list.add(new Mahasiswa("mhs-07", "1301213007", "Fajar Nugraha", "fajar.nugraha@student.ac.id",
                "2023", "Teknik Informatika", "IF-45-01", 88, Mahasiswa.StatusMahasiswa.AKTIF,
                Arrays.asList("mk-pbo-01")));
        list.add(new Mahasiswa("mhs-08", "1301213008", "Gita Clarissa", "gita.clarissa@student.ac.id",
                "2023", "Teknik Informatika", "IF-45-01", 95, Mahasiswa.StatusMahasiswa.AKTIF,
                Arrays.asList("mk-pbo-01", "mk-rpl-01")));
        list.add(new Mahasiswa("mhs-09", "1301213009", "Hafiz Danendra", "hafiz.d@student.ac.id",
                "2023", "Teknik Informatika", "IF-45-01", 65, Mahasiswa.StatusMahasiswa.BERISIKO,
                Arrays.asList("mk-pbo-01")));
        list.add(new Mahasiswa("mhs-10", "1301213010", "Nadia Putri Salsabila", "nadia.s@student.ac.id",
                "2023", "Teknik Komputer", "IF-45-02", 92, Mahasiswa.StatusMahasiswa.AKTIF,
                Arrays.asList("mk-pbo-02", "mk-rpl-01")));
        return list;
    }

    public List<NilaiMahasiswa> createDemoNilai() throws ValidasiInputException {
        List<NilaiMahasiswa> list = new ArrayList<>();
        list.add(new NilaiMahasiswa("n-01", "mhs-01", "mk-pbo-01",
                mapOf("k-pbo-tugas", 88.0, "k-pbo-lab", 90.0, "k-pbo-uts", 84.0, "k-pbo-proyek", 92.0, "k-pbo-uas", 86.0),
                "Sangat aktif dalam diskusi OOP.", "2025-05-10"));
        list.add(new NilaiMahasiswa("n-02", "mhs-02", "mk-pbo-01",
                mapOf("k-pbo-tugas", 95.0, "k-pbo-lab", 94.0, "k-pbo-uts", 90.0, "k-pbo-proyek", 96.0, "k-pbo-uas", 91.0),
                "Performa terbaik di kelas IF-45-01.", "2025-05-10"));
        list.add(new NilaiMahasiswa("n-03", "mhs-03", "mk-pbo-01",
                mapOf("k-pbo-tugas", 76.0, "k-pbo-lab", 78.0, "k-pbo-uts", 72.0, "k-pbo-proyek", 80.0, "k-pbo-uas", 74.0),
                "Konsisten dan cukup baik.", "2025-05-09"));
        list.add(new NilaiMahasiswa("n-04", "mhs-04", "mk-pbo-01",
                mapOf("k-pbo-tugas", 82.0, "k-pbo-lab", 85.0, "k-pbo-uts", 78.0, "k-pbo-proyek", 84.0, "k-pbo-uas", 80.0),
                "Desain class diagram rapi.", "2025-05-09"));
        list.add(new NilaiMahasiswa("n-05", "mhs-05", "mk-pbo-01",
                mapOf("k-pbo-tugas", 58.0, "k-pbo-lab", 55.0, "k-pbo-uts", 52.0, "k-pbo-proyek", 60.0, "k-pbo-uas", 50.0),
                "Perlu bimbingan remedial UTS/UAS.", "2025-05-08"));
        list.add(new NilaiMahasiswa("n-06", "mhs-06", "mk-pbo-01",
                mapOf("k-pbo-tugas", 90.0, "k-pbo-lab", 88.0, "k-pbo-uts", 86.0, "k-pbo-proyek", 90.0, "k-pbo-uas", 89.0),
                "Implementasi GUI sangat baik.", "2025-05-10"));
        list.add(new NilaiMahasiswa("n-07", "mhs-07", "mk-pbo-01",
                mapOf("k-pbo-tugas", 70.0, "k-pbo-lab", 74.0, "k-pbo-uts", 68.0, "k-pbo-proyek", 75.0, "k-pbo-uas", 72.0),
                "", "2025-05-07"));
        list.add(new NilaiMahasiswa("n-08", "mhs-08", "mk-pbo-01",
                mapOf("k-pbo-tugas", 85.0, "k-pbo-lab", 82.0, "k-pbo-uts", 80.0, "k-pbo-proyek", 86.0, "k-pbo-uas", 84.0),
                "", "2025-05-07"));
        list.add(new NilaiMahasiswa("n-09", "mhs-09", "mk-pbo-01",
                mapOf("k-pbo-tugas", 45.0, "k-pbo-lab", 48.0, "k-pbo-uts", 40.0, "k-pbo-proyek", 50.0, "k-pbo-uas", 42.0),
                "Kehadiran di bawah 75%, wajib menghadap dosen wali.", "2025-05-06"));
        return list;
    }

    public List<AktivitasLog> createDemoAktivitas() {
        List<AktivitasLog> list = new ArrayList<>();
        list.add(new AktivitasLog("act-1", "Hari ini, 09:30", "Inisialisasi Sistem LECTURA", "SISTEM",
                "Memuat data akademik Semester Genap 2024/2025"));
        list.add(new AktivitasLog("act-2", "Hari ini, 09:45", "Pembaruan Nilai IF-45-01", "NILAI",
                "Nilai proyek akhir Pemrograman Berorientasi Objek diperbarui"));
        return list;
    }

    private Map<String, Double> mapOf(String k1, double v1, String k2, double v2,
                                      String k3, double v3, String k4, double v4,
                                      String k5, double v5) {
        Map<String, Double> m = new LinkedHashMap<>();
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        m.put(k5, v5);
        return m;
    }
}
