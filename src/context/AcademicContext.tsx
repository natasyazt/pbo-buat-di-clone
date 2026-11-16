import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  Dosen,
  MataKuliah,
  Mahasiswa,
  KomponenPenilaian,
  NilaiMahasiswa,
  AktivitasLog,
  RekapNilaiDetail,
} from '../types/academic';
import {
  initialDosen,
  initialMataKuliah,
  initialKomponen,
  initialMahasiswa,
  initialNilai,
  initialAktivitas,
} from '../data/initialData';
import { hitungNilaiMahasiswa } from '../utils/gradeCalculator';

export type ActiveView = 'dashboard' | 'matakuliah' | 'mahasiswa' | 'komponen' | 'input-nilai';

interface AcademicContextType {
  // Auth
  dosen: Dosen;
  isLoggedIn: boolean;
  login: (email: string, pass: string) => boolean;
  logout: () => void;

  // Navigation
  currentView: ActiveView;
  setCurrentView: (view: ActiveView) => void;

  // Selected filter
  selectedMataKuliahId: string;
  setSelectedMataKuliahId: (id: string) => void;

  // Mata Kuliah CRUD
  mataKuliahList: MataKuliah[];
  addMataKuliah: (mk: Omit<MataKuliah, 'id'>) => void;
  updateMataKuliah: (id: string, mk: Partial<MataKuliah>) => void;
  deleteMataKuliah: (id: string) => void;

  // Mahasiswa CRUD
  mahasiswaList: Mahasiswa[];
  addMahasiswa: (mhs: Omit<Mahasiswa, 'id'>) => void;
  updateMahasiswa: (id: string, mhs: Partial<Mahasiswa>) => void;
  deleteMahasiswa: (id: string) => void;

  // Komponen CRUD
  komponenList: KomponenPenilaian[];
  getKomponenByMataKuliah: (mkId: string) => KomponenPenilaian[];
  addKomponen: (komp: Omit<KomponenPenilaian, 'id'>) => void;
  updateKomponen: (id: string, komp: Partial<KomponenPenilaian>) => void;
  deleteKomponen: (id: string) => void;

  // Nilai
  nilaiList: NilaiMahasiswa[];
  updateNilaiSingle: (
    mahasiswaId: string,
    mataKuliahId: string,
    komponenId: string,
    nilai: number,
    catatan?: string
  ) => void;
  updateCatatanDosen: (
    mahasiswaId: string,
    mataKuliahId: string,
    catatan: string
  ) => void;
  getRekapNilaiByMataKuliah: (mkId: string) => RekapNilaiDetail[];

  // Logs
  aktivitasList: AktivitasLog[];
  addAktivitas: (aksi: string, kategori: AktivitasLog['kategori'], detail: string) => void;

  // Reset
  resetData: () => void;
}

const AcademicContext = createContext<AcademicContextType | undefined>(undefined);

const STORAGE_KEY = 'lectura_academic_db_v1';

export const AcademicProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Load saved state or default
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_auth`);
    return saved ? JSON.parse(saved) : true; // Default true so user directly sees the full app, can logout
  });

  const [currentView, setCurrentView] = useState<ActiveView>('dashboard');
  const [dosen] = useState<Dosen>(initialDosen);

  const [mataKuliahList, setMataKuliahList] = useState<MataKuliah[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_mk`);
    return saved ? JSON.parse(saved) : initialMataKuliah;
  });

  const [selectedMataKuliahId, setSelectedMataKuliahId] = useState<string>(() => {
    return initialMataKuliah[0]?.id || '';
  });

  const [komponenList, setKomponenList] = useState<KomponenPenilaian[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_komponen`);
    return saved ? JSON.parse(saved) : initialKomponen;
  });

  const [mahasiswaList, setMahasiswaList] = useState<Mahasiswa[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_mahasiswa`);
    return saved ? JSON.parse(saved) : initialMahasiswa;
  });

  const [nilaiList, setNilaiList] = useState<NilaiMahasiswa[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_nilai`);
    return saved ? JSON.parse(saved) : initialNilai;
  });

  const [aktivitasList, setAktivitasList] = useState<AktivitasLog[]>(() => {
    const saved = localStorage.getItem(`${STORAGE_KEY}_aktivitas`);
    return saved ? JSON.parse(saved) : initialAktivitas;
  });

  // Save changes to localStorage
  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_auth`, JSON.stringify(isLoggedIn));
  }, [isLoggedIn]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_mk`, JSON.stringify(mataKuliahList));
  }, [mataKuliahList]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_komponen`, JSON.stringify(komponenList));
  }, [komponenList]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_mahasiswa`, JSON.stringify(mahasiswaList));
  }, [mahasiswaList]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_nilai`, JSON.stringify(nilaiList));
  }, [nilaiList]);

  useEffect(() => {
    localStorage.setItem(`${STORAGE_KEY}_aktivitas`, JSON.stringify(aktivitasList));
  }, [aktivitasList]);

  const addAktivitas = (aksi: string, kategori: AktivitasLog['kategori'], detail: string) => {
    const newLog: AktivitasLog = {
      id: `act-${Date.now()}`,
      waktu: 'Baru saja',
      aksi,
      kategori,
      detail,
    };
    setAktivitasList((prev) => [newLog, ...prev.slice(0, 20)]);
  };

  const login = (email: string, _pass: string) => {
    if (email) {
      setIsLoggedIn(true);
      setCurrentView('dashboard');
      addAktivitas('Dosen Berhasil Masuk', 'NILAI', `Dosen ${dosen.nama} masuk ke sistem.`);
      return true;
    }
    return false;
  };

  const logout = () => {
    setIsLoggedIn(false);
  };

  // Mata Kuliah CRUD
  const addMataKuliah = (mk: Omit<MataKuliah, 'id'>) => {
    const newId = `mk-${Date.now()}`;
    const newMK: MataKuliah = { ...mk, id: newId };
    setMataKuliahList((prev) => [...prev, newMK]);
    
    // Automatically create default components for the new course
    const defaultKomps: KomponenPenilaian[] = [
      { id: `k-${newId}-tugas`, mataKuliahId: newId, nama: 'Tugas Mandiri', bobot: 20, kkm: 60 },
      { id: `k-${newId}-prak`, mataKuliahId: newId, nama: 'Praktikum', bobot: 25, kkm: 65 },
      { id: `k-${newId}-uts`, mataKuliahId: newId, nama: 'UTS', bobot: 25, kkm: 60 },
      { id: `k-${newId}-uas`, mataKuliahId: newId, nama: 'UAS', bobot: 30, kkm: 60 },
    ];
    setKomponenList((prev) => [...prev, ...defaultKomps]);

    addAktivitas(
      'Menambahkan Mata Kuliah',
      'MATA_KULIAH',
      `Menambahkan ${mk.kode} - ${mk.nama} (Kelas ${mk.kelas})`
    );
  };

  const updateMataKuliah = (id: string, updated: Partial<MataKuliah>) => {
    setMataKuliahList((prev) =>
      prev.map((mk) => (mk.id === id ? { ...mk, ...updated } : mk))
    );
    addAktivitas(
      'Memperbarui Mata Kuliah',
      'MATA_KULIAH',
      `Memperbarui informasi mata kuliah ID: ${id}`
    );
  };

  const deleteMataKuliah = (id: string) => {
    const target = mataKuliahList.find((m) => m.id === id);
    setMataKuliahList((prev) => prev.filter((m) => m.id !== id));
    setKomponenList((prev) => prev.filter((k) => k.mataKuliahId !== id));
    setNilaiList((prev) => prev.filter((n) => n.mataKuliahId !== id));
    if (selectedMataKuliahId === id) {
      const remaining = mataKuliahList.filter((m) => m.id !== id);
      if (remaining.length > 0) {
        setSelectedMataKuliahId(remaining[0].id);
      }
    }
    addAktivitas(
      'Menghapus Mata Kuliah',
      'MATA_KULIAH',
      `Menghapus mata kuliah ${target?.nama || id}`
    );
  };

  // Mahasiswa CRUD
  const addMahasiswa = (mhs: Omit<Mahasiswa, 'id'>) => {
    const newId = `mhs-${Date.now()}`;
    const newMhs: Mahasiswa = { ...mhs, id: newId };
    setMahasiswaList((prev) => [...prev, newMhs]);
    addAktivitas(
      'Mendaftarkan Mahasiswa',
      'MAHASISWA',
      `Mendaftarkan ${mhs.nama} (${mhs.nim}) ke kelas ${mhs.kelas}`
    );
  };

  const updateMahasiswa = (id: string, updated: Partial<Mahasiswa>) => {
    setMahasiswaList((prev) =>
      prev.map((m) => (m.id === id ? { ...m, ...updated } : m))
    );
    addAktivitas(
      'Memperbarui Data Mahasiswa',
      'MAHASISWA',
      `Memperbarui profil mahasiswa ID: ${id}`
    );
  };

  const deleteMahasiswa = (id: string) => {
    const target = mahasiswaList.find((m) => m.id === id);
    setMahasiswaList((prev) => prev.filter((m) => m.id !== id));
    setNilaiList((prev) => prev.filter((n) => n.mahasiswaId !== id));
    addAktivitas(
      'Menghapus Mahasiswa',
      'MAHASISWA',
      `Menghapus ${target?.nama || id} dari daftar mahasiswa`
    );
  };

  // Komponen Penilaian CRUD
  const getKomponenByMataKuliah = (mkId: string) => {
    return komponenList.filter((k) => k.mataKuliahId === mkId);
  };

  const addKomponen = (komp: Omit<KomponenPenilaian, 'id'>) => {
    const newId = `k-${Date.now()}`;
    const newKomp: KomponenPenilaian = { ...komp, id: newId };
    setKomponenList((prev) => [...prev, newKomp]);
    addAktivitas(
      'Menambah Komponen Penilaian',
      'KOMPONEN',
      `Menambahkan komponen "${komp.nama}" dengan bobot ${komp.bobot}%`
    );
  };

  const updateKomponen = (id: string, updated: Partial<KomponenPenilaian>) => {
    setKomponenList((prev) =>
      prev.map((k) => (k.id === id ? { ...k, ...updated } : k))
    );
    addAktivitas(
      'Mengubah Komponen Penilaian',
      'KOMPONEN',
      `Mengubah parameter komponen penilaian ID: ${id}`
    );
  };

  const deleteKomponen = (id: string) => {
    const target = komponenList.find((k) => k.id === id);
    setKomponenList((prev) => prev.filter((k) => k.id !== id));
    // Clean up student scores for this deleted component
    setNilaiList((prev) =>
      prev.map((n) => {
        const copy = { ...n.nilaiKomponen };
        delete copy[id];
        return { ...n, nilaiKomponen: copy };
      })
    );
    addAktivitas(
      'Menghapus Komponen Penilaian',
      'KOMPONEN',
      `Menghapus komponen ${target?.nama || id}`
    );
  };

  // Nilai Management
  const updateNilaiSingle = (
    mahasiswaId: string,
    mataKuliahId: string,
    komponenId: string,
    nilai: number,
    catatan?: string
  ) => {
    const clamped = Math.max(0, Math.min(100, isNaN(nilai) ? 0 : nilai));
    setNilaiList((prev) => {
      const existingIndex = prev.findIndex(
        (n) => n.mahasiswaId === mahasiswaId && n.mataKuliahId === mataKuliahId
      );

      if (existingIndex >= 0) {
        const updated = [...prev];
        const record = updated[existingIndex];
        updated[existingIndex] = {
          ...record,
          nilaiKomponen: {
            ...record.nilaiKomponen,
            [komponenId]: clamped,
          },
          catatanDosen: catatan !== undefined ? catatan : record.catatanDosen,
          terakhirDiubah: new Date().toLocaleString('id-ID'),
        };
        return updated;
      } else {
        const newRecord: NilaiMahasiswa = {
          id: `nil-${Date.now()}-${Math.random().toString(36).substr(2, 4)}`,
          mahasiswaId,
          mataKuliahId,
          nilaiKomponen: {
            [komponenId]: clamped,
          },
          catatanDosen: catatan || '',
          terakhirDiubah: new Date().toLocaleString('id-ID'),
        };
        return [...prev, newRecord];
      }
    });
  };

  const updateCatatanDosen = (
    mahasiswaId: string,
    mataKuliahId: string,
    catatan: string
  ) => {
    setNilaiList((prev) => {
      const idx = prev.findIndex(
        (n) => n.mahasiswaId === mahasiswaId && n.mataKuliahId === mataKuliahId
      );
      if (idx >= 0) {
        const updated = [...prev];
        updated[idx] = { ...updated[idx], catatanDosen: catatan };
        return updated;
      } else {
        return [
          ...prev,
          {
            id: `nil-${Date.now()}`,
            mahasiswaId,
            mataKuliahId,
            nilaiKomponen: {},
            catatanDosen: catatan,
            terakhirDiubah: new Date().toLocaleString('id-ID'),
          },
        ];
      }
    });
  };

  const getRekapNilaiByMataKuliah = (mkId: string): RekapNilaiDetail[] => {
    const enrolledStudents = mahasiswaList.filter((m) =>
      m.mataKuliahIds.includes(mkId)
    );
    const komps = getKomponenByMataKuliah(mkId);

    return enrolledStudents.map((mhs) => {
      const nilaiRec = nilaiList.find(
        (n) => n.mahasiswaId === mhs.id && n.mataKuliahId === mkId
      );
      return hitungNilaiMahasiswa(mhs, komps, nilaiRec);
    });
  };

  const resetData = () => {
    localStorage.removeItem(`${STORAGE_KEY}_mk`);
    localStorage.removeItem(`${STORAGE_KEY}_komponen`);
    localStorage.removeItem(`${STORAGE_KEY}_mahasiswa`);
    localStorage.removeItem(`${STORAGE_KEY}_nilai`);
    localStorage.removeItem(`${STORAGE_KEY}_aktivitas`);
    setMataKuliahList(initialMataKuliah);
    setSelectedMataKuliahId(initialMataKuliah[0]?.id || '');
    setKomponenList(initialKomponen);
    setMahasiswaList(initialMahasiswa);
    setNilaiList(initialNilai);
    setAktivitasList(initialAktivitas);
    addAktivitas('Reset Data Sistem', 'NILAI', 'Mengembalikan seluruh data ke kondisi awal pengujian.');
  };

  return (
    <AcademicContext.Provider
      value={{
        dosen,
        isLoggedIn,
        login,
        logout,
        currentView,
        setCurrentView,
        selectedMataKuliahId,
        setSelectedMataKuliahId,
        mataKuliahList,
        addMataKuliah,
        updateMataKuliah,
        deleteMataKuliah,
        mahasiswaList,
        addMahasiswa,
        updateMahasiswa,
        deleteMahasiswa,
        komponenList,
        getKomponenByMataKuliah,
        addKomponen,
        updateKomponen,
        deleteKomponen,
        nilaiList,
        updateNilaiSingle,
        updateCatatanDosen,
        getRekapNilaiByMataKuliah,
        aktivitasList,
        addAktivitas,
        resetData,
      }}
    >
      {children}
    </AcademicContext.Provider>
  );
};

export const useAcademic = () => {
  const context = useContext(AcademicContext);
  if (!context) {
    throw new Error('useAcademic must be used within an AcademicProvider');
  }
  return context;
};
