export interface Dosen {
  id: string;
  nip: string;
  nama: string;
  gelar: string;
  email: string;
  prodi: string;
  fakultas: string;
  avatarUrl?: string;
}

export interface MataKuliah {
  id: string;
  kode: string;
  nama: string;
  sks: number;
  semester: string; // e.g. "Genap 2024/2025"
  kelas: string; // e.g. "IF-45-01"
  ruang: string;
  jadwal: string;
  deskripsi?: string;
}

export interface Mahasiswa {
  id: string;
  nim: string;
  nama: string;
  email: string;
  angkatan: string;
  prodi: string;
  kelas: string;
  mataKuliahIds: string[]; // Mata kuliah yang diambil
  kehadiranPersen: number; // e.g. 92
  status: 'Aktif' | 'Cuti' | 'Berisiko' | 'Non-Aktif';
}

export interface KomponenPenilaian {
  id: string;
  mataKuliahId: string;
  nama: string; // e.g. "Tugas Mandiri", "Praktikum PBO", "UTS", "UAS", "Kuis"
  bobot: number; // persentase, e.g. 20
  kkm: number; // Kriteria Ketuntasan Minimal, default 55
  keterangan?: string;
}

export interface NilaiMahasiswa {
  id: string;
  mahasiswaId: string;
  mataKuliahId: string;
  nilaiKomponen: Record<string, number>; // komponenId -> nilai (0-100)
  catatanDosen?: string;
  terakhirDiubah?: string;
}

export interface RekapNilaiDetail {
  mahasiswa: Mahasiswa;
  nilaiKomponen: Record<string, number>;
  nilaiAkhir: number;
  hurufMutu: 'A' | 'AB' | 'B' | 'BC' | 'C' | 'D' | 'E';
  indeks: number; // 4.0, 3.5, 3.0, 2.5, 2.0, 1.0, 0.0
  statusLulus: 'LULUS' | 'REMIDI' | 'TIDAK LULUS';
  catatanDosen?: string;
}

export interface AktivitasLog {
  id: string;
  waktu: string;
  aksi: string;
  kategori: 'NILAI' | 'MAHASISWA' | 'MATA_KULIAH' | 'KOMPONEN';
  detail: string;
}
