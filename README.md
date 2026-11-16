# LECTURA — Sistem Penilaian Akademik Dosen

Aplikasi web modern berbasis **React, TypeScript, dan Tailwind CSS** yang dirancang khusus untuk dosen dalam mengamati performa belajar mahasiswa, mengonfigurasi skema evaluasi bobot, serta menginput dan merekapitulasi nilai akhir secara real-time.

---

## 🎨 Palet Warna Baru (Custom Color Palette)

Aplikasi telah diperbarui dengan palet warna khusus sesuai spesifikasi visual:

| Kode Warna | Nama Nuansa | Penggunaan dalam UI |
| :--- | :--- | :--- |
| **`#355C7D`** | *Deep Twilight Blue* | Sidebar navigasi utama, tombol primer, header tabel, identitas brand utama |
| **`#6C5B7B`** | *Purple Slate / Lilac* | Elemen sekunder, border kartu, teks label, ikon pendukung, tab aktif |
| **`#C06C84`** | *Dusty Rose / Mauve* | Tombol sekunder, indikator fokus, batas tabel, badge status khusus |
| **`#F67280`** | *Coral Pink* | Aksen peringatan remidi, sorotan nilai di bawah KKM, indikator bobot belum 100% |
| **`#F8B195`** | *Soft Peach* | Monogram brand "S", lencana semester aktif, latar belakang lembut kartu metrik |

---

## 🚀 Fitur Utama

### 1. Portal Autentikasi Dosen (`LoginSignup`)
- Desain antarmuka akademik premium dengan gradien halus `#355C7D` ke `#6C5B7B` dan aksen `#F8B195`.
- Login aman dengan validasi email institusi (`rina.puspita@sipena.ac.id`) dan opsi *Ingat Saya*.
- Fitur **Masuk Cepat (1-Click Demo Login)** sebagai Dra. Rina Puspita, M.Kom.
- Dialog bantuan layanan IT Administrator kampus.

### 2. Dashboard Akademik & Monitoring (`AcademicDashboardSection`)
- **4 Kartu Metrik Utama**: Total Peserta Kelas, Rata-rata Nilai Kelas, Persentase Kelulusan, dan Status Kelengkapan Bobot (100%).
- **Distribusi Huruf Mutu**: Visualisasi diagram batang sebaran nilai A, AB, B, BC, C, D, dan E dengan palet warna terpadu.
- **Monitoring Mahasiswa Berisiko**: Peringatan dini (*early warning alert*) untuk mahasiswa dengan nilai < KKM atau presensi kehadiran < 75%.
- **Ringkasan Mata Kuliah**: Daftar kelas yang diampu dengan pemilih cepat (*quick course switcher*).
- **Log Aktivitas Sistem**: Riwayat pencatatan penilaian dan perubahan bobot terkini.

### 3. Manajemen Mata Kuliah (`CourseManagementSection`)
- Kelola seluruh mata kuliah yang diampu (Kode MK, Nama MK, SKS, Semester, Kelas, Jadwal, dan Ruang Lab/Kelas).
- Tambah, Edit, dan Hapus mata kuliah dengan konfirmasi aman.
- Pencarian cerdas berdasarkan kode MK, nama mata kuliah, atau kelas.
- Tombol pintasan langsung ke pengaturan bobot dan lembar input nilai tiap kelas.

### 4. Manajemen Mahasiswa & Presensi (`StudentManagementSection`)
- Direktori mahasiswa lengkap dengan NIM, Nama Lengkap, Email, Angkatan, Kelas, dan Status.
- Indikator visual persentase presensi kehadiran mahasiswa dengan peringatan kehadiran rendah (< 75%).
- **Impor Cepat (Batch Import)**: Salin dan tempel daftar nama & NIM langsung dari Excel/Word/Spreadsheet.
- **Ekspor Data Mahasiswa**: Unduh daftar mahasiswa per kelas dalam format `.csv`.
- Tambah, Edit, dan Hapus data mahasiswa.

### 5. Komponen & Bobot Penilaian (`AssessmentComponentsSection`)
- Skema evaluasi fleksibel per mata kuliah (Tugas Mandiri, Praktikum Modul PBO, Kuis, UTS, UAS, Proyek Akhir).
- **Validasi Akumulasi Bobot 100%**: Indikator progress bar otomatis yang memberi tahu jika bobot masih kurang atau berlebih.
- Konfigurasi KKM (Kriteria Ketuntasan Minimal) per komponen.
- **Template Bobot Cepat**: Preset siap pakai untuk format praktikum/lab maupun format teori reguler.
- Tabel referensi standar skala nilai mutu institusi (A = 4.0 s.d. E = 0.0).

### 6. Input & Rekapitulasi Nilai Real-Time (`GradeSummarySection`)
- **Spreadsheet Interaktif**: Kolom komponen dinamis sesuai mata kuliah yang dipilih.
- **Kalkulasi Nilai Akhir Otomatis**: Nilai akhir tertimbang dihitung secara real-time saat dosen mengetik angka:
  $$\text{Nilai Akhir} = \sum (\text{Nilai}_i \times \text{Bobot}_i)$$
- **Konversi Huruf Mutu & Indeks Otomatis**:
  - $\ge 80.0$: **A** (Indeks 4.0, LULUS)
  - $75.0 - 79.9$: **AB** (Indeks 3.5, LULUS)
  - $70.0 - 74.9$: **B** (Indeks 3.0, LULUS)
  - $65.0 - 69.9$: **BC** (Indeks 2.5, LULUS)
  - $60.0 - 64.9$: **C** (Indeks 2.0, LULUS)
  - $50.0 - 59.9$: **D** (Indeks 1.0, REMIDI)
  - $< 50.0$: **E** (Indeks 0.0, TIDAK LULUS)
- Peringatan visual (cell warna `#F67280`/20) jika nilai mahasiswa di bawah KKM.
- Kolom catatan khusus dosen untuk instruksi remedial / perbaikan.
- **Fitur Simulasi Nilai (Auto-fill Demo)**: Mengisi nilai otomatis untuk presentasi atau pengujian.
- **Ekspor CSV**: Unduh file rekapitulasi nilai siap buka di Microsoft Excel.
- **Lembar Cetak Transkrip Nilai Resmi**: Tampilan cetak ber-KOP institusi kementerian lengkap dengan kolom tanda tangan Ketua Program Studi dan Dosen Pengampu (siap cetak fisik atau simpan PDF).

### 7. Penyimpanan Data Lokal (LocalStorage Persistence)
- Seluruh input nilai, mahasiswa baru, perubahan bobot, dan modifikasi mata kuliah otomatis tersimpan di penyimpanan browser (`localStorage`).
- Tombol **Reset Demo** di bagian bawah sidebar untuk mengembalikan seluruh data ke kondisi awal pengujian kapan saja.

---

## 💻 Cara Menjalankan Aplikasi

Pastikan **Node.js** telah terinstal di komputer.

### Cara 1: Menggunakan File Batch (Paling Praktis)
Cukup klik ganda (double-click) file **`run.bat`** di folder proyek. Browser akan terbuka otomatis di `http://localhost:3000`.

### Cara 2: Melalui Terminal / Command Prompt
```bash
# Jalankan mode pengembangan
npm run dev

# Atau buka server di jaringan lokal
npm run dev -- --host
```

### Cara 3: Build untuk Produksi
```bash
npm run build
npm run preview
```

---

© 2025 LECTURA — Sistem Penilaian Akademik Dosen
