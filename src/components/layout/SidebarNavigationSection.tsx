import React from 'react';
import {
  LayoutDashboard,
  BookOpen,
  Users,
  Sliders,
  FileSpreadsheet,
  LogOut,
  GraduationCap,
  ChevronRight,
  RotateCcw,
} from 'lucide-react';
import { useAcademic, ActiveView } from '../../context/AcademicContext';

export const SidebarNavigationSection: React.FC = () => {
  const {
    currentView,
    setCurrentView,
    dosen,
    logout,
    mataKuliahList,
    mahasiswaList,
    selectedMataKuliahId,
    setSelectedMataKuliahId,
    resetData,
  } = useAcademic();

  const navItems: { id: ActiveView; label: string; icon: React.ReactNode; badge?: string }[] = [
    {
      id: 'dashboard',
      label: 'Ringkasan Akademik',
      icon: <LayoutDashboard className="w-4 h-4" />,
    },
    {
      id: 'matakuliah',
      label: 'Mata Kuliah Diampu',
      icon: <BookOpen className="w-4 h-4" />,
      badge: `${mataKuliahList.length} MK`,
    },
    {
      id: 'mahasiswa',
      label: 'Daftar Mahasiswa',
      icon: <Users className="w-4 h-4" />,
      badge: `${mahasiswaList.length} Mhs`,
    },
    {
      id: 'komponen',
      label: 'Komponen & Bobot',
      icon: <Sliders className="w-4 h-4" />,
    },
    {
      id: 'input-nilai',
      label: 'Input & Rekap Nilai',
      icon: <FileSpreadsheet className="w-4 h-4" />,
      badge: 'Aktif',
    },
  ];

  const handleReset = () => {
    if (window.confirm('Kembalikan data ke nilai awal demo? Semua input tambahan akan di-reset.')) {
      resetData();
    }
  };

  return (
    <aside
      aria-label="Navigasi Utama"
      className="w-[280px] min-w-[280px] bg-[#355C7D] text-[#ffffff] flex flex-col justify-between border-r border-[#2b4c68] min-h-[641px] select-none"
    >
      {/* Header & Logo */}
      <div className="p-6">
        <div className="flex items-center gap-3">
          <div className="w-[40px] h-[40px] rounded-[8px] bg-[#F8B195] text-[#355C7D] font-serif font-bold text-2xl flex items-center justify-center shadow-md">
            S
          </div>
          <div>
            <h1 className="text-[20px] font-serif tracking-[0.8px] font-bold text-white leading-tight">
              LECTURA
            </h1>
            <p className="text-[10px] text-[#F8B195] tracking-wide font-medium">
              Sistem Penilaian Akademik
            </p>
          </div>
        </div>

        {/* Academic Period Badge */}
        <div className="mt-5 p-2.5 rounded-lg bg-[#28465f] border border-[#3e6689] flex items-center justify-between">
          <div>
            <div className="text-[8px] uppercase tracking-[1.2px] font-extrabold text-[#F8B195]">
              PORTAL AKADEMIK DOSEN
            </div>
            <div className="text-[11px] font-medium text-white mt-0.5 flex items-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-[#F67280] animate-pulse"></span>
              Genap 2024/2025
            </div>
          </div>
          <GraduationCap className="w-4 h-4 text-[#F8B195]" />
        </div>

        {/* Quick Course Switcher */}
        <div className="mt-4">
          <label className="text-[9px] font-bold uppercase tracking-wider text-[#d0dfec] block mb-1.5">
            Mata Kuliah Aktif:
          </label>
          <div className="relative">
            <select
              value={selectedMataKuliahId}
              onChange={(e) => setSelectedMataKuliahId(e.target.value)}
              className="w-full bg-[#28465f] text-white text-[11px] rounded border border-[#3e6689] py-1.5 px-2.5 focus:outline-none focus:border-[#F67280] cursor-pointer appearance-none truncate font-medium"
            >
              {mataKuliahList.map((mk) => (
                <option key={mk.id} value={mk.id} className="bg-[#355C7D] text-white">
                  {mk.kode} - {mk.nama} ({mk.kelas})
                </option>
              ))}
            </select>
            <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-[#F8B195]">
              <ChevronRight className="w-3.5 h-3.5 rotate-90" />
            </div>
          </div>
        </div>

        {/* Navigation Items */}
        <nav className="mt-6 flex flex-col gap-1.5">
          <div className="text-[9px] font-bold uppercase tracking-wider text-[#b8d1e6] px-2 mb-1">
            Menu Perkuliahan
          </div>
          {navItems.map((item) => {
            const isActive = currentView === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setCurrentView(item.id)}
                className={`flex items-center justify-between px-3.5 py-2.5 rounded-lg text-[12px] font-medium transition-all ${
                  isActive
                    ? 'bg-[#6C5B7B] text-white font-bold border-l-4 border-[#F67280] shadow-sm'
                    : 'text-[#e5eff8] hover:bg-[#28465f] hover:text-white'
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <span className={isActive ? 'text-[#F8B195]' : 'text-[#b8d1e6]'}>
                    {item.icon}
                  </span>
                  <span>{item.label}</span>
                </div>
                {item.badge && (
                  <span
                    className={`text-[9px] px-1.5 py-0.5 rounded font-mono font-bold ${
                      isActive
                        ? 'bg-[#F67280] text-white'
                        : 'bg-[#28465f] text-[#F8B195]'
                    }`}
                  >
                    {item.badge}
                  </span>
                )}
              </button>
            );
          })}
        </nav>
      </div>

      {/* Footer & Lecturer Profile Card */}
      <div className="p-4 border-t border-[#2b4c68] bg-[#28465f]">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-full bg-[#F8B195] text-[#355C7D] flex items-center justify-center font-bold text-xs ring-2 ring-[#F67280]">
            RP
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-[11px] font-bold text-white truncate">
              {dosen.nama}
            </p>
            <p className="text-[9px] text-[#b8d1e6] truncate">
              NIP: {dosen.nip}
            </p>
            <p className="text-[9px] text-[#F8B195] truncate font-semibold">
              {dosen.gelar}
            </p>
          </div>
        </div>

        <div className="mt-3 pt-3 border-t border-[#355877] flex items-center justify-between text-[10px]">
          <button
            onClick={handleReset}
            title="Reset data ke default"
            className="text-[#b8d1e6] hover:text-[#F8B195] flex items-center gap-1 transition-colors font-medium"
          >
            <RotateCcw className="w-3 h-3" />
            <span>Reset Demo</span>
          </button>

          <button
            onClick={logout}
            className="text-[#F67280] hover:text-[#F8B195] font-bold flex items-center gap-1 transition-colors"
          >
            <LogOut className="w-3 h-3" />
            <span>Keluar</span>
          </button>
        </div>

        <div className="mt-2 text-center text-[8px] text-[#86a6c3]">
          © 2025 LECTURA · Dosen Portal v2.4
        </div>
      </div>
    </aside>
  );
};

export const NavigationSidebarSection = SidebarNavigationSection;
export default SidebarNavigationSection;
