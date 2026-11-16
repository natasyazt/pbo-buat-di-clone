import React from 'react';
import {
  Users,
  Award,
  TrendingUp,
  AlertTriangle,
  CheckCircle2,
  BookOpen,
  ArrowRight,
  Clock,
  Sparkles,
  BarChart3,
  FileSpreadsheet,
  GraduationCap,
} from 'lucide-react';
import { useAcademic } from '../../context/AcademicContext';
import { getGradeBadgeColor, getStatusBadgeColor } from '../../utils/gradeCalculator';

export const AcademicDashboardSection: React.FC = () => {
  const {
    dosen,
    mataKuliahList,
    mahasiswaList,
    selectedMataKuliahId,
    setSelectedMataKuliahId,
    setCurrentView,
    getRekapNilaiByMataKuliah,
    getKomponenByMataKuliah,
    aktivitasList,
  } = useAcademic();

  const currentCourse =
    mataKuliahList.find((mk) => mk.id === selectedMataKuliahId) ||
    mataKuliahList[0];

  const currentRekap = currentCourse
    ? getRekapNilaiByMataKuliah(currentCourse.id)
    : [];

  const currentKomponen = currentCourse
    ? getKomponenByMataKuliah(currentCourse.id)
    : [];

  const totalBobot = currentKomponen.reduce((acc, k) => acc + k.bobot, 0);

  // Statistics
  const totalStudents = currentRekap.length;
  const avgScore = totalStudents > 0
    ? (currentRekap.reduce((acc, r) => acc + r.nilaiAkhir, 0) / totalStudents).toFixed(1)
    : '0';

  const lulusCount = currentRekap.filter((r) => r.statusLulus === 'LULUS').length;
  const remidiCount = currentRekap.filter((r) => r.statusLulus === 'REMIDI').length;
  const tidakLulusCount = currentRekap.filter((r) => r.statusLulus === 'TIDAK LULUS').length;
  const passRate = totalStudents > 0 ? ((lulusCount / totalStudents) * 100).toFixed(0) : '0';

  // Grade distribution counts
  const gradeDistribution = {
    A: currentRekap.filter((r) => r.hurufMutu === 'A').length,
    AB: currentRekap.filter((r) => r.hurufMutu === 'AB').length,
    B: currentRekap.filter((r) => r.hurufMutu === 'B').length,
    BC: currentRekap.filter((r) => r.hurufMutu === 'BC').length,
    C: currentRekap.filter((r) => r.hurufMutu === 'C').length,
    D: currentRekap.filter((r) => r.hurufMutu === 'D').length,
    E: currentRekap.filter((r) => r.hurufMutu === 'E').length,
  };

  // At-risk students
  const atRiskStudents = currentRekap.filter(
    (r) => r.statusLulus !== 'LULUS' || r.mahasiswa.kehadiranPersen < 75
  );

  return (
    <div className="flex-1 min-h-[641px] bg-[#fbfafc] text-[#355C7D] p-6 lg:p-8 overflow-y-auto">
      {/* Top Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between pb-6 border-b border-[#6C5B7B]/20 gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-extrabold uppercase tracking-widest bg-[#355C7D] text-[#F8B195] px-2.5 py-0.5 rounded-full">
              LECTURA DASHBOARD
            </span>
            <span className="text-xs text-[#6C5B7B] font-medium">
              • Semester Genap 2024/2025
            </span>
          </div>
          <h2 className="text-2xl font-serif font-bold text-[#355C7D] mt-1.5">
            Selamat Datang, {dosen.nama}
          </h2>
          <p className="text-xs text-[#6C5B7B] mt-0.5">
            Portal penilaian dan monitoring hasil studi mahasiswa Program Studi {dosen.prodi}.
          </p>
        </div>

        {/* Quick Actions */}
        <div className="flex items-center gap-2.5">
          <button
            onClick={() => setCurrentView('input-nilai')}
            className="flex items-center gap-2 px-4 py-2 rounded-lg bg-[#355C7D] text-white text-xs font-semibold hover:bg-[#28465f] transition-all shadow-sm"
          >
            <FileSpreadsheet className="w-3.5 h-3.5 text-[#F8B195]" />
            <span>Input & Rekap Nilai</span>
          </button>
          <button
            onClick={() => setCurrentView('matakuliah')}
            className="flex items-center gap-2 px-3.5 py-2 rounded-lg border border-[#C06C84] bg-white text-xs font-semibold hover:bg-[#F8B195]/20 text-[#C06C84] transition-all shadow-sm"
          >
            <BookOpen className="w-3.5 h-3.5" />
            <span>Kelola MK</span>
          </button>
        </div>
      </div>

      {/* Course Context Bar */}
      <div className="my-5 p-3.5 rounded-xl bg-white border border-[#6C5B7B]/20 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-lg bg-[#355C7D] text-[#F8B195] flex items-center justify-center font-bold text-xs shadow-xs">
            MK
          </div>
          <div>
            <div className="text-[10px] uppercase font-bold text-[#C06C84] tracking-wider">
              Mata Kuliah Terpilih
            </div>
            <div className="text-sm font-bold text-[#355C7D]">
              {currentCourse?.kode} — {currentCourse?.nama}{' '}
              <span className="text-xs font-medium text-[#6C5B7B]">
                ({currentCourse?.kelas})
              </span>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="text-right hidden sm:block">
            <span className="text-[10px] text-[#6C5B7B] block font-medium">Status Bobot:</span>
            <span
              className={`text-xs font-bold ${totalBobot === 100 ? 'text-[#355C7D]' : 'text-[#F67280]'
                }`}
            >
              {totalBobot === 100 ? '✓ Lengkap 100%' : `⚠ Belum 100% (${totalBobot}%)`}
            </span>
          </div>
          <select
            value={selectedMataKuliahId}
            onChange={(e) => setSelectedMataKuliahId(e.target.value)}
            aria-label="Pilih Mata Kuliah untuk Ditampilkan di Dashboard"
            className="bg-[#fbfafc] text-xs font-semibold text-[#355C7D] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 focus:outline-none focus:border-[#355C7D]"
          >
            {mataKuliahList.map((mk) => (
              <option key={mk.id} value={mk.id}>
                {mk.kode} - {mk.nama} ({mk.kelas})
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* 4 Primary Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Card 1: Students */}
        <div className="bg-white p-4 rounded-xl border border-[#6C5B7B]/20 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold uppercase tracking-wider text-[#6C5B7B]">
              Peserta Kelas
            </span>
            <div className="w-8 h-8 rounded-lg bg-[#355C7D]/10 flex items-center justify-center text-[#355C7D]">
              <Users className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-2xl font-serif font-bold text-[#355C7D]">
              {totalStudents}
            </span>
            <span className="text-xs text-[#6C5B7B]">Mahasiswa</span>
          </div>
          <div className="mt-2 pt-2 border-t border-[#f2edf4] text-[10px] text-[#6C5B7B] flex justify-between">
            <span>SKS: {currentCourse?.sks || 3} SKS</span>
            <span className="text-[#355C7D] font-bold">100% Terdaftar</span>
          </div>
        </div>

        {/* Card 2: Average Score */}
        <div className="bg-white p-4 rounded-xl border border-[#6C5B7B]/20 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold uppercase tracking-wider text-[#6C5B7B]">
              Rata-rata Nilai
            </span>
            <div className="w-8 h-8 rounded-lg bg-[#6C5B7B]/10 flex items-center justify-center text-[#6C5B7B]">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-2xl font-serif font-bold text-[#355C7D]">
              {avgScore}
            </span>
            <span className="text-xs text-[#6C5B7B] font-medium">/ 100</span>
          </div>
          <div className="mt-2 pt-2 border-t border-[#f2edf4] text-[10px] text-[#6C5B7B] flex justify-between">
            <span>Standar Kelulusan: ≥ 60.0</span>
            <span className="font-bold text-[#355C7D]">Memuaskan</span>
          </div>
        </div>

        {/* Card 3: Pass Rate */}
        <div className="bg-white p-4 rounded-xl border border-[#6C5B7B]/20 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold uppercase tracking-wider text-[#6C5B7B]">
              Tingkat Kelulusan
            </span>
            <div className="w-8 h-8 rounded-lg bg-[#C06C84]/15 flex items-center justify-center text-[#C06C84]">
              <Award className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-2xl font-serif font-bold text-[#355C7D]">
              {passRate}%
            </span>
            <span className="text-xs text-[#355C7D] font-medium">({lulusCount} Lulus)</span>
          </div>
          <div className="mt-2 pt-2 border-t border-[#f2edf4] text-[10px] text-[#6C5B7B] flex justify-between">
            <span className="text-[#F67280] font-semibold">Remidi: {remidiCount}</span>
            <span className="text-[#C06C84] font-bold">Gagal: {tidakLulusCount}</span>
          </div>
        </div>

        {/* Card 4: Grading Components */}
        <div className="bg-white p-4 rounded-xl border border-[#6C5B7B]/20 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold uppercase tracking-wider text-[#6C5B7B]">
              Komponen Penilaian
            </span>
            <div className="w-8 h-8 rounded-lg bg-[#F8B195]/30 flex items-center justify-center text-[#355C7D]">
              <CheckCircle2 className="w-4 h-4 text-[#355C7D]" />
            </div>
          </div>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-2xl font-serif font-bold text-[#355C7D]">
              {currentKomponen.length}
            </span>
            <span className="text-xs text-[#6C5B7B]">Parameter</span>
          </div>
          <div className="mt-2 pt-2 border-t border-[#f2edf4] text-[10px] text-[#6C5B7B] flex justify-between">
            <span>Total Bobot:</span>
            <span
              className={`font-bold ${totalBobot === 100 ? 'text-[#355C7D]' : 'text-[#F67280]'
                }`}
            >
              {totalBobot}%
            </span>
          </div>
        </div>
      </div>

      {/* Middle Section: Grade Distribution & At-Risk Warning */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
        {/* Grade Distribution Bar Chart */}
        <div className="lg:col-span-2 bg-white p-5 rounded-xl border border-[#6C5B7B]/20 shadow-sm">
          <div className="flex items-center justify-between pb-3 border-b border-[#f2edf4]">
            <div>
              <h3 className="text-sm font-bold text-[#355C7D] flex items-center gap-2">
                <BarChart3 className="w-4 h-4 text-[#C06C84]" />
                Distribusi Huruf Mutu ({currentCourse?.kode})
              </h3>
              <p className="text-[11px] text-[#6C5B7B]">
                Sebaran perolehan nilai akhir mahasiswa berdasarkan skala mutu institusi.
              </p>
            </div>
            <button
              onClick={() => setCurrentView('input-nilai')}
              className="text-[11px] font-bold text-[#C06C84] hover:text-[#355C7D] flex items-center gap-1 transition-colors"
            >
              <span>Lihat Detail</span>
              <ArrowRight className="w-3 h-3" />
            </button>
          </div>

          <div className="mt-4 grid grid-cols-7 gap-2 text-center">
            {Object.entries(gradeDistribution).map(([grade, count]) => {
              const percentage = totalStudents > 0 ? ((count / totalStudents) * 100).toFixed(0) : 0;
              const badge = getGradeBadgeColor(grade);

              // Color mapping from palette
              const barColor =
                grade === 'A'
                  ? 'bg-[#355C7D]'
                  : grade === 'AB'
                    ? 'bg-[#4a6d8c]'
                    : grade === 'B'
                      ? 'bg-[#6C5B7B]'
                      : grade === 'BC'
                        ? 'bg-[#857297]'
                        : grade === 'C'
                          ? 'bg-[#F8B195]'
                          : grade === 'D'
                            ? 'bg-[#F67280]'
                            : 'bg-[#C06C84]';

              return (
                <div key={grade} className="flex flex-col items-center">
                  <span className="text-[10px] font-semibold text-[#6C5B7B] mb-1">
                    {count} mhs
                  </span>
                  {/* Visual Bar */}
                  <div className="w-full bg-[#f2edf4] h-32 rounded-lg flex items-end p-1 relative group">
                    <div
                      style={{ height: `${Math.max(14, Number(percentage))}%` }}
                      className={`w-full rounded-md transition-all duration-500 ${barColor}`}
                    ></div>
                    <div className="absolute inset-x-0 -top-7 hidden group-hover:flex justify-center">
                      <span className="bg-[#355C7D] text-white text-[9px] px-1.5 py-0.5 rounded shadow whitespace-nowrap font-bold">
                        {percentage}%
                      </span>
                    </div>
                  </div>
                  <div className="mt-2">
                    <span
                      className={`inline-block px-2 py-0.5 text-xs font-bold rounded border ${badge.bg} ${badge.text} ${badge.border}`}
                    >
                      {grade}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>

          <div className="mt-5 pt-3 border-t border-[#f2edf4] flex flex-wrap items-center justify-between text-[11px] text-[#6C5B7B] gap-2">
            <div className="flex items-center gap-4">
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-sm bg-[#355C7D]"></span>
                Sangat Baik (A/AB)
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-sm bg-[#6C5B7B]"></span>
                Baik (B/BC)
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-sm bg-[#F8B195]"></span>
                Cukup (C)
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-sm bg-[#F67280]"></span>
                Remidi / Gagal (D/E)
              </span>
            </div>
            <span className="text-[10px] text-[#6C5B7B] font-medium">
              Total Sampel: {totalStudents} Data Mahasiswa
            </span>
          </div>
        </div>

        {/* At-Risk Warning Box */}
        <div className="bg-white p-5 rounded-xl border border-[#6C5B7B]/20 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between pb-3 border-b border-[#f2edf4]">
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-[#F67280]" />
                <h3 className="text-sm font-bold text-[#355C7D]">
                  Monitoring Mahasiswa Berisiko
                </h3>
              </div>
              <span className="text-[10px] font-bold bg-[#F67280]/20 text-[#C06C84] px-2 py-0.5 rounded-full">
                {atRiskStudents.length} Mahasiswa
              </span>
            </div>

            <p className="text-[11px] text-[#6C5B7B] mt-2 mb-3">
              Mahasiswa dengan nilai di bawah KKM (&lt; 60) atau kehadiran &lt; 75%.
            </p>

            <div className="space-y-2.5 max-h-[220px] overflow-y-auto pr-1">
              {atRiskStudents.length === 0 ? (
                <div className="text-center py-8 text-[#6C5B7B]">
                  <CheckCircle2 className="w-8 h-8 text-[#355C7D] mx-auto mb-1.5" />
                  <p className="text-xs font-semibold text-[#355C7D]">
                    Semua Mahasiswa Memenuhi Standar
                  </p>
                  <p className="text-[10px]">Tidak ada mahasiswa yang terancam remidi saat ini.</p>
                </div>
              ) : (
                atRiskStudents.map((item) => {
                  const statusBadge = getStatusBadgeColor(item.statusLulus);
                  return (
                    <div
                      key={item.mahasiswa.id}
                      className="p-2.5 rounded-lg bg-[#fbfafc] border border-[#6C5B7B]/15 flex items-center justify-between text-xs hover:border-[#C06C84] transition-colors"
                    >
                      <div>
                        <p className="font-bold text-[#355C7D] text-[11px] truncate max-w-[170px]">
                          {item.mahasiswa.nama}
                        </p>
                        <p className="text-[9px] text-[#6C5B7B]">
                          NIM: {item.mahasiswa.nim} • Hadir {item.mahasiswa.kehadiranPersen}%
                        </p>
                      </div>
                      <div className="text-right">
                        <span className="text-xs font-bold text-[#355C7D]">
                          {item.nilaiAkhir.toFixed(1)}
                        </span>
                        <div className="mt-0.5">
                          <span
                            className={`text-[9px] font-extrabold px-1.5 py-0.5 rounded ${statusBadge.bg} ${statusBadge.text}`}
                          >
                            {item.statusLulus} ({item.hurufMutu})
                          </span>
                        </div>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>

          <div className="mt-4 pt-3 border-t border-[#f2edf4]">
            <button
              onClick={() => setCurrentView('input-nilai')}
              className="w-full py-2 rounded-lg bg-[#355C7D] hover:bg-[#28465f] text-white text-xs font-semibold flex items-center justify-center gap-1.5 transition-all shadow-sm"
            >
              <span>Buka Lembar Rekap Nilai</span>
              <ArrowRight className="w-3.5 h-3.5 text-[#F8B195]" />
            </button>
          </div>
        </div>
      </div>

      {/* Bottom Section: Courses List & Audit Log */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
        {/* Courses Overview */}
        <div className="lg:col-span-2 bg-white p-5 rounded-xl border border-[#6C5B7B]/20 shadow-sm">
          <div className="flex items-center justify-between pb-3 border-b border-[#f2edf4]">
            <div>
              <h3 className="text-sm font-bold text-[#355C7D] flex items-center gap-2">
                <BookOpen className="w-4 h-4 text-[#C06C84]" />
                Mata Kuliah Semester Ini
              </h3>
              <p className="text-[11px] text-[#6C5B7B]">
                Daftar kelas yang diampu oleh {dosen.nama} pada periode berjalan.
              </p>
            </div>
            <button
              onClick={() => setCurrentView('matakuliah')}
              className="text-[11px] font-bold text-[#C06C84] hover:underline"
            >
              Kelola Semua
            </button>
          </div>

          <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-3">
            {mataKuliahList.map((mk) => {
              const enrolled = mahasiswaList.filter((m) =>
                m.mataKuliahIds.includes(mk.id)
              ).length;
              const komps = getKomponenByMataKuliah(mk.id);
              const isSelected = mk.id === selectedMataKuliahId;

              return (
                <div
                  key={mk.id}
                  onClick={() => setSelectedMataKuliahId(mk.id)}
                  className={`p-3.5 rounded-xl border transition-all cursor-pointer ${isSelected
                    ? 'bg-[#F8B195]/15 border-[#355C7D] ring-2 ring-[#355C7D]/30'
                    : 'bg-white border-[#6C5B7B]/20 hover:border-[#C06C84]'
                    }`}
                >
                  <div className="flex items-start justify-between">
                    <div>
                      <span className="text-[9px] font-extrabold uppercase px-2 py-0.5 rounded-full bg-[#355C7D] text-[#F8B195]">
                        {mk.kode} • {mk.sks} SKS
                      </span>
                      <h4 className="font-bold text-xs text-[#355C7D] mt-1 line-clamp-1">
                        {mk.nama}
                      </h4>
                    </div>
                    <span className="text-xs font-bold text-[#6C5B7B] bg-[#f2edf4] px-2 py-0.5 rounded">
                      {mk.kelas}
                    </span>
                  </div>

                  <p className="text-[10px] text-[#6C5B7B] mt-2 line-clamp-1 font-medium">
                    {mk.jadwal} • {mk.ruang}
                  </p>

                  <div className="mt-3 pt-2.5 border-t border-[#f2edf4] flex items-center justify-between text-[10px] text-[#6C5B7B]">
                    <span>Peserta: {enrolled} Mhs</span>
                    <span className="font-bold text-[#355C7D]">{komps.length} Komponen Bobot</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Audit Trail */}
        <div className="bg-white p-5 rounded-xl border border-[#6C5B7B]/20 shadow-sm">
          <div className="flex items-center justify-between pb-3 border-b border-[#f2edf4]">
            <div className="flex items-center gap-2">
              <Clock className="w-4 h-4 text-[#C06C84]" />
              <h3 className="text-sm font-bold text-[#355C7D]">Aktivitas Penilaian</h3>
            </div>
            <span className="text-[9px] text-[#6C5B7B]">Log Sistem</span>
          </div>

          <div className="mt-3 space-y-3">
            {aktivitasList.slice(0, 5).map((act) => (
              <div key={act.id} className="flex items-start gap-2.5 text-xs">
                <span className="w-2 h-2 rounded-full bg-[#C06C84] mt-1.5 flex-shrink-0"></span>
                <div>
                  <div className="flex items-center gap-2">
                    <p className="font-bold text-[#355C7D] text-[11px]">{act.aksi}</p>
                    <span className="text-[8px] text-[#6C5B7B]">• {act.waktu}</span>
                  </div>
                  <p className="text-[10px] text-[#6C5B7B] mt-0.5 line-clamp-2">
                    {act.detail}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AcademicDashboardSection;
