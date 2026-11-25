import React, { useState } from 'react';
import {
  FileSpreadsheet,
  Download,
  Printer,
  Sparkles,
  Save,
  Search,
  CheckCircle2,
  AlertTriangle,
  HelpCircle,
  FileText,
  X,
  Filter,
} from 'lucide-react';
import { useAcademic } from '../../context/AcademicContext';
import { getGradeBadgeColor, getStatusBadgeColor } from '../../utils/gradeCalculator';

export const GradeSummarySection: React.FC = () => {
  const {
    dosen,
    mataKuliahList,
    selectedMataKuliahId,
    setSelectedMataKuliahId,
    getKomponenByMataKuliah,
    getRekapNilaiByMataKuliah,
    updateNilaiSingle,
    updateCatatanDosen,
    addAktivitas,
  } = useAcademic();

  const [searchQuery, setSearchQuery] = useState('');
  const [filterMode, setFilterMode] = useState<'ALL' | 'INCOMPLETE' | 'AT_RISK'>('ALL');
  const [isPrintModalOpen, setIsPrintModalOpen] = useState(false);
  const [saveToast, setSaveToast] = useState(false);

  const currentCourse =
    mataKuliahList.find((mk) => mk.id === selectedMataKuliahId) ||
    mataKuliahList[0];

  const komponenList = currentCourse
    ? getKomponenByMataKuliah(currentCourse.id)
    : [];

  const totalBobot = komponenList.reduce((acc, k) => acc + k.bobot, 0);

  const rekapList = currentCourse
    ? getRekapNilaiByMataKuliah(currentCourse.id)
    : [];

  // Summary statistics
  const totalStudents = rekapList.length;
  const avgScore = totalStudents > 0
    ? (rekapList.reduce((acc, r) => acc + r.nilaiAkhir, 0) / totalStudents).toFixed(1)
    : '0';

  const scores = rekapList.map((r) => r.nilaiAkhir);
  const maxScore = scores.length > 0 ? Math.max(...scores).toFixed(1) : '0';
  const minScore = scores.length > 0 ? Math.min(...scores).toFixed(1) : '0';

  const lulusCount = rekapList.filter((r) => r.statusLulus === 'LULUS').length;
  const remidiCount = rekapList.filter((r) => r.statusLulus === 'REMIDI').length;
  const tidakLulusCount = rekapList.filter((r) => r.statusLulus === 'TIDAK LULUS').length;

  // Filtered rows
  const filteredRows = rekapList.filter((row) => {
    const matchesSearch =
      row.mahasiswa.nama.toLowerCase().includes(searchQuery.toLowerCase()) ||
      row.mahasiswa.nim.toLowerCase().includes(searchQuery.toLowerCase());

    if (!matchesSearch) return false;

    if (filterMode === 'AT_RISK') {
      return row.statusLulus !== 'LULUS' || row.mahasiswa.kehadiranPersen < 75;
    }

    if (filterMode === 'INCOMPLETE') {
      return komponenList.some((k) => row.nilaiKomponen[k.id] === undefined);
    }

    return true;
  });

  const handleScoreChange = (
    mahasiswaId: string,
    komponenId: string,
    valueStr: string
  ) => {
    const num = parseFloat(valueStr);
    const validNum = isNaN(num) ? 0 : Math.min(100, Math.max(0, num));
    updateNilaiSingle(mahasiswaId, currentCourse.id, komponenId, validNum);
  };

  const handleNoteChange = (mahasiswaId: string, note: string) => {
    updateCatatanDosen(mahasiswaId, currentCourse.id, note);
  };

  // Quick save feedback
  const triggerSaveNotification = () => {
    addAktivitas(
      'Menyimpan Lembar Nilai',
      'NILAI',
      `Menyimpan seluruh nilai mahasiswa kelas ${currentCourse.kelas} (${currentCourse.kode})`
    );
    setSaveToast(true);
    setTimeout(() => setSaveToast(false), 3000);
  };

  // Auto-fill random realistic scores for demo
  const handleAutoFillDemo = () => {
    if (
      !window.confirm(
        'Isi nilai otomatis untuk seluruh mahasiswa yang belum lengkap nilainya?'
      )
    )
      return;

    rekapList.forEach((r) => {
      komponenList.forEach((k) => {
        if (r.nilaiKomponen[k.id] === undefined || r.nilaiKomponen[k.id] === 0) {
          const randomScore = Math.floor(Math.random() * 31) + 65;
          updateNilaiSingle(r.mahasiswa.id, currentCourse.id, k.id, randomScore);
        }
      });
    });
    triggerSaveNotification();
  };

  // Export CSV
  const handleExportCSV = () => {
    if (!currentCourse) return;
    const header = [
      'No',
      'NIM',
      'Nama Mahasiswa',
      'Kehadiran (%)',
      ...komponenList.map((k) => `"${k.nama} (${k.bobot}%)"`),
      'Nilai Akhir',
      'Huruf Mutu',
      'Indeks',
      'Status Kelulusan',
      'Catatan Dosen',
    ];

    const rows = rekapList.map((r, idx) => {
      const compScores = komponenList.map((k) => r.nilaiKomponen[k.id] ?? 0);
      return [
        idx + 1,
        `"${r.mahasiswa.nim}"`,
        `"${r.mahasiswa.nama}"`,
        r.mahasiswa.kehadiranPersen,
        ...compScores,
        r.nilaiAkhir,
        `"${r.hurufMutu}"`,
        r.indeks,
        `"${r.statusLulus}"`,
        `"${r.catatanDosen || ''}"`,
      ];
    });

    const csvContent =
      'data:text/csv;charset=utf-8,' +
      [header.join(','), ...rows.map((row) => row.join(','))].join('\n');

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute(
      'download',
      `Rekap_Nilai_${currentCourse.kode}_${currentCourse.kelas}.csv`
    );
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="flex-1 min-h-[641px] bg-[#fbfafc] text-[#355C7D] p-6 lg:p-8 overflow-y-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between pb-6 border-b border-[#6C5B7B]/20 gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-extrabold uppercase tracking-widest bg-[#355C7D] text-[#F8B195] px-2.5 py-0.5 rounded-full">
              GRADEBOOK RESMI
            </span>
            <span className="text-xs text-[#6C5B7B] font-medium">
              • Lembar Penilaian & Rekapitulasi
            </span>
          </div>
          <h2 className="text-2xl font-serif font-bold text-[#355C7D] mt-1.5">
            Input & Rekapitulasi Nilai Mahasiswa
          </h2>
          <p className="text-xs text-[#6C5B7B] mt-0.5">
            Kalkulasi otomatis nilai akhir tertimbang secara real-time berdasarkan bobot komponen yang dikonfigurasi.
          </p>
        </div>

        {/* Global Actions */}
        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={handleAutoFillDemo}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-[#C06C84] bg-white text-xs font-semibold hover:bg-[#F8B195]/20 text-[#C06C84] transition-all shadow-sm"
            title="Isi otomatis nilai mahasiswa untuk simulasi"
          >
            <Sparkles className="w-3.5 h-3.5 text-[#F67280]" />
            <span>Simulasi Nilai</span>
          </button>
          <button
            onClick={handleExportCSV}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-[#355C7D]/30 bg-white text-xs font-semibold hover:bg-[#355C7D]/10 text-[#355C7D] transition-all shadow-sm"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Ekspor CSV</span>
          </button>
          <button
            onClick={() => setIsPrintModalOpen(true)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-[#6C5B7B]/30 bg-white text-xs font-semibold hover:bg-[#6C5B7B]/10 text-[#6C5B7B] transition-all shadow-sm"
          >
            <Printer className="w-3.5 h-3.5" />
            <span>Cetak Rekap</span>
          </button>
          <button
            onClick={triggerSaveNotification}
            className="flex items-center gap-1.5 px-4 py-1.5 rounded-lg bg-[#355C7D] text-white text-xs font-semibold hover:bg-[#28465f] transition-all shadow-sm"
          >
            <Save className="w-3.5 h-3.5 text-[#F8B195]" />
            <span>Simpan Nilai</span>
          </button>
        </div>
      </div>

      {/* Save Notification Toast */}
      {saveToast && (
        <div className="my-3 p-3 bg-[#355C7D]/10 border border-[#355C7D] rounded-lg text-[#355C7D] text-xs flex items-center justify-between animate-in fade-in">
          <div className="flex items-center gap-2 font-bold">
            <CheckCircle2 className="w-4 h-4 text-[#355C7D]" />
            <span>Semua nilai mahasiswa telah tersimpan ke basis data lokal secara otomatis!</span>
          </div>
          <span className="text-[10px] text-[#6C5B7B] font-semibold">Tersinkronisasi</span>
        </div>
      )}

      {/* Course Bar & Quick Stats */}
      <div className="my-5 p-4 rounded-xl bg-white border border-[#6C5B7B]/20 shadow-sm">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-lg bg-[#355C7D] text-[#F8B195] flex items-center justify-center font-serif font-bold text-base shadow-xs">
              MK
            </div>
            <div>
              <div className="flex items-center gap-2">
                <select
                  value={selectedMataKuliahId}
                  onChange={(e) => setSelectedMataKuliahId(e.target.value)}
                  className="bg-[#fbfafc] text-xs font-bold text-[#355C7D] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 focus:outline-none"
                >
                  {mataKuliahList.map((mk) => (
                    <option key={mk.id} value={mk.id}>
                      {mk.kode} - {mk.nama} ({mk.kelas})
                    </option>
                  ))}
                </select>
                <span className="text-xs text-[#6C5B7B]">
                  {currentCourse?.sks} SKS • {currentCourse?.semester}
                </span>
              </div>
              <p className="text-[11px] text-[#6C5B7B] mt-0.5">
                Ruang: {currentCourse?.ruang} • Jadwal: {currentCourse?.jadwal}
              </p>
            </div>
          </div>

          {/* Quick Metrics */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-center border-t lg:border-t-0 pt-3 lg:pt-0">
            <div className="p-2.5 rounded-lg bg-[#fbfafc] border border-[#6C5B7B]/15">
              <span className="text-[9px] uppercase font-bold text-[#6C5B7B] block">
                Rata-rata Kelas
              </span>
              <span className="text-base font-serif font-bold text-[#355C7D]">
                {avgScore}
              </span>
            </div>
            <div className="p-2.5 rounded-lg bg-[#fbfafc] border border-[#6C5B7B]/15">
              <span className="text-[9px] uppercase font-bold text-[#6C5B7B] block">
                Nilai Tertinggi
              </span>
              <span className="text-base font-serif font-bold text-[#355C7D]">
                {maxScore}
              </span>
            </div>
            <div className="p-2.5 rounded-lg bg-[#fbfafc] border border-[#6C5B7B]/15">
              <span className="text-[9px] uppercase font-bold text-[#6C5B7B] block">
                Nilai Terendah
              </span>
              <span className="text-base font-serif font-bold text-[#F67280]">
                {minScore}
              </span>
            </div>
            <div className="p-2.5 rounded-lg bg-[#fbfafc] border border-[#6C5B7B]/15">
              <span className="text-[9px] uppercase font-bold text-[#6C5B7B] block">
                Kelulusan
              </span>
              <span className="text-base font-serif font-bold text-[#355C7D]">
                {lulusCount}{' '}
                <span className="text-xs font-normal text-[#6C5B7B]">
                  / {totalStudents}
                </span>
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="mb-4 flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-xs font-bold text-[#6C5B7B]">Tampilkan:</span>
          <button
            onClick={() => setFilterMode('ALL')}
            className={`px-3 py-1 rounded-lg text-xs font-semibold border transition-colors ${
              filterMode === 'ALL'
                ? 'bg-[#355C7D] text-white border-[#355C7D]'
                : 'bg-white text-[#6C5B7B] border-[#6C5B7B]/30 hover:bg-[#fbfafc]'
            }`}
          >
            Semua ({totalStudents})
          </button>
          <button
            onClick={() => setFilterMode('AT_RISK')}
            className={`px-3 py-1 rounded-lg text-xs font-semibold border transition-colors ${
              filterMode === 'AT_RISK'
                ? 'bg-[#F67280] text-white border-[#F67280]'
                : 'bg-white text-[#6C5B7B] border-[#6C5B7B]/30 hover:bg-[#fbfafc]'
            }`}
          >
            Berisiko / Remidi ({remidiCount + tidakLulusCount})
          </button>
        </div>

        <div className="relative w-full sm:w-64">
          <Search className="w-3.5 h-3.5 absolute left-3 top-2.5 text-[#C06C84]" />
          <input
            type="text"
            placeholder="Filter NIM / Nama..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-white text-xs text-[#355C7D] border border-[#6C5B7B]/30 rounded-lg pl-8 pr-3 py-1.5 focus:outline-none focus:border-[#355C7D]"
          />
        </div>
      </div>

      {/* Main Grade Spreadsheet */}
      <div className="bg-white rounded-xl border border-[#6C5B7B]/20 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-[#355C7D] text-white text-[10px] tracking-wider uppercase font-semibold">
                <th className="py-3 px-3 text-center w-10">No</th>
                <th className="py-3 px-3 min-w-[100px]">NIM</th>
                <th className="py-3 px-3 min-w-[170px]">Nama Mahasiswa</th>
                <th className="py-3 px-3 text-center min-w-[70px]">Hadir</th>

                {/* Dynamic Component Columns */}
                {komponenList.map((k) => (
                  <th
                    key={k.id}
                    className="py-3 px-2 text-center min-w-[95px] bg-[#28465f] border-l border-[#3a5e7e]"
                  >
                    <div className="font-bold truncate" title={k.nama}>
                      {k.nama}
                    </div>
                    <div className="text-[9px] text-[#F8B195] font-mono">
                      Bobot {k.bobot}%
                    </div>
                  </th>
                ))}

                {/* Final Calculated Columns */}
                <th className="py-3 px-3 text-center min-w-[85px] bg-[#20394f] text-[#F8B195] border-l border-[#3a5e7e]">
                  Nilai Akhir
                </th>
                <th className="py-3 px-2 text-center min-w-[65px] bg-[#20394f]">
                  Huruf
                </th>
                <th className="py-3 px-3 text-center min-w-[95px] bg-[#20394f]">
                  Status
                </th>
                <th className="py-3 px-3 min-w-[200px]">Catatan Dosen</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#f2edf4] text-[#355C7D]">
              {filteredRows.length === 0 ? (
                <tr>
                  <td
                    colSpan={8 + komponenList.length}
                    className="py-12 text-center text-[#6C5B7B]"
                  >
                    <FileSpreadsheet className="w-8 h-8 mx-auto mb-2 opacity-40 text-[#355C7D]" />
                    <p className="font-semibold text-xs">Tidak ada data mahasiswa pada kriteria ini.</p>
                  </td>
                </tr>
              ) : (
                filteredRows.map((row, idx) => {
                  const gradeBadge = getGradeBadgeColor(row.hurufMutu);
                  const statusBadge = getStatusBadgeColor(row.statusLulus);

                  return (
                    <tr
                      key={row.mahasiswa.id}
                      className="hover:bg-[#F8B195]/10 transition-colors"
                    >
                      <td className="py-2.5 px-3 text-center font-mono text-[11px] text-[#6C5B7B]">
                        {idx + 1}
                      </td>
                      <td className="py-2.5 px-3 font-mono font-bold text-[#355C7D]">
                        {row.mahasiswa.nim}
                      </td>
                      <td className="py-2.5 px-3">
                        <div className="font-bold text-xs text-[#355C7D]">
                          {row.mahasiswa.nama}
                        </div>
                        <div className="text-[10px] text-[#6C5B7B]">
                          {row.mahasiswa.kelas}
                        </div>
                      </td>
                      <td className="py-2.5 px-3 text-center font-medium">
                        <span
                          className={
                            row.mahasiswa.kehadiranPersen < 75
                              ? 'text-[#F67280] font-bold'
                              : 'text-[#355C7D]'
                          }
                        >
                          {row.mahasiswa.kehadiranPersen}%
                        </span>
                      </td>

                      {/* Interactive Component Input Cells */}
                      {komponenList.map((k) => {
                        const currentVal = row.nilaiKomponen[k.id];
                        const isUnderKKM = currentVal !== undefined && currentVal < k.kkm;

                        return (
                          <td
                            key={k.id}
                            className="py-2 px-2 text-center border-l border-[#f2edf4]"
                          >
                            <input
                              type="number"
                              min="0"
                              max="100"
                              step="1"
                              value={currentVal !== undefined ? currentVal : ''}
                              onChange={(e) =>
                                handleScoreChange(
                                  row.mahasiswa.id,
                                  k.id,
                                  e.target.value
                                )
                              }
                              placeholder="0"
                              className={`w-16 text-center py-1 rounded-md text-xs font-mono font-bold border focus:outline-none transition-all ${
                                isUnderKKM
                                  ? 'border-[#F67280] bg-[#F67280]/15 text-[#C06C84] focus:border-[#C06C84]'
                                  : 'border-[#6C5B7B]/30 bg-white text-[#355C7D] focus:border-[#355C7D]'
                              }`}
                            />
                          </td>
                        );
                      })}

                      {/* Final Score */}
                      <td className="py-2.5 px-3 text-center font-serif font-bold text-sm text-[#355C7D] bg-[#fbfafc] border-l border-[#f2edf4]">
                        {row.nilaiAkhir.toFixed(1)}
                      </td>

                      {/* Huruf Mutu */}
                      <td className="py-2.5 px-2 text-center bg-[#fbfafc]">
                        <span
                          className={`inline-block px-2 py-0.5 rounded text-[10px] font-extrabold border ${gradeBadge.bg} ${gradeBadge.text} ${gradeBadge.border}`}
                        >
                          {row.hurufMutu}
                        </span>
                      </td>

                      {/* Status Kelulusan */}
                      <td className="py-2.5 px-3 text-center bg-[#fbfafc]">
                        <span
                          className={`inline-block px-2 py-0.5 rounded-full text-[9px] font-extrabold ${statusBadge.bg} ${statusBadge.text}`}
                        >
                          {row.statusLulus}
                        </span>
                      </td>

                      {/* Catatan Dosen */}
                      <td className="py-2 px-3">
                        <input
                          type="text"
                          value={row.catatanDosen || ''}
                          onChange={(e) =>
                            handleNoteChange(row.mahasiswa.id, e.target.value)
                          }
                          placeholder="Catatan kemajuan / remidi..."
                          className="w-full bg-transparent border-b border-transparent hover:border-[#6C5B7B]/30 focus:border-[#355C7D] focus:bg-white text-[11px] py-1 px-1.5 focus:outline-none rounded transition-colors text-[#6C5B7B]"
                        />
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Footer Info */}
        <div className="p-3 bg-[#fbfafc] border-t border-[#6C5B7B]/20 flex flex-wrap items-center justify-between text-xs text-[#6C5B7B] gap-2">
          <div className="flex items-center gap-3">
            <span>
              Menampilkan <span className="font-bold text-[#355C7D]">{filteredRows.length}</span> dari {totalStudents} mahasiswa
            </span>
            <span>•</span>
            <span className="text-[#355C7D] font-bold">
              Otomatis tersimpan di browser
            </span>
          </div>

          <div className="text-[11px] text-[#6C5B7B]">
            Dosen Pengampu: {dosen.nama} (NIP: {dosen.nip})
          </div>
        </div>
      </div>

      {/* Official Print & PDF Modal */}
      {isPrintModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-y-auto p-8 border border-[#6C5B7B]/30">
            {/* Action Bar */}
            <div className="flex items-center justify-between pb-4 mb-6 border-b border-[#f2edf4] print:hidden">
              <div className="flex items-center gap-2">
                <FileText className="w-5 h-5 text-[#355C7D]" />
                <h3 className="font-serif font-bold text-base text-[#355C7D]">
                  Lembar Cetak Transkrip Nilai Akademik
                </h3>
              </div>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => window.print()}
                  className="px-4 py-1.5 rounded-lg bg-[#355C7D] text-white text-xs font-semibold flex items-center gap-1.5 hover:bg-[#28465f]"
                >
                  <Printer className="w-3.5 h-3.5 text-[#F8B195]" />
                  <span>Cetak / Simpan PDF</span>
                </button>
                <button
                  onClick={() => setIsPrintModalOpen(false)}
                  className="p-1.5 text-[#6C5B7B] hover:text-[#355C7D]"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* Letterhead */}
            <div className="text-center pb-4 border-b-2 border-[#355C7D]">
              <div className="font-serif font-extrabold text-lg uppercase tracking-wider text-[#355C7D]">
                KEMENTERIAN PENDIDIKAN TINGGI, SAINS DAN TEKNOLOGI
              </div>
              <div className="font-serif font-bold text-base text-[#6C5B7B]">
                FAKULTAS TEKNIK KOMPUTER & REKAYASA
              </div>
              <div className="text-xs text-[#355C7D] mt-0.5 font-medium">
                PROGRAM STUDI S1 TEKNIK KOMPUTER — SISTEM PENILAIAN LECTURA
              </div>
              <div className="text-[10px] text-[#6C5B7B] mt-0.5">
                Jl. Pendidikan Akademik No. 1 • Telp (021) 7891234 • sipena.ac.id
              </div>
            </div>

            <div className="text-center my-4">
              <h4 className="font-serif font-bold text-sm tracking-wide uppercase text-[#355C7D]">
                DAFTAR NILAI AKHIR MAHASISWA
              </h4>
              <p className="text-xs text-[#6C5B7B]">
                SEMESTER GENAP TAHUN AKADEMIK 2024/2025
              </p>
            </div>

            {/* Course Meta Info */}
            <div className="grid grid-cols-2 gap-2 text-xs mb-4 p-3 bg-[#fbfafc] rounded-lg border border-[#6C5B7B]/20">
              <div>
                <p>
                  <span className="font-bold text-[#355C7D]">Mata Kuliah:</span>{' '}
                  {currentCourse?.nama} ({currentCourse?.kode})
                </p>
                <p>
                  <span className="font-bold text-[#355C7D]">Beban SKS:</span>{' '}
                  {currentCourse?.sks} SKS
                </p>
                <p>
                  <span className="font-bold text-[#355C7D]">Kelas / Ruang:</span>{' '}
                  {currentCourse?.kelas} / {currentCourse?.ruang}
                </p>
              </div>
              <div>
                <p>
                  <span className="font-bold text-[#355C7D]">Dosen Pengampu:</span>{' '}
                  {dosen.nama}
                </p>
                <p>
                  <span className="font-bold text-[#355C7D]">NIP:</span> {dosen.nip}
                </p>
                <p>
                  <span className="font-bold text-[#355C7D]">Waktu Cetak:</span>{' '}
                  {new Date().toLocaleDateString('id-ID', {
                    day: 'numeric',
                    month: 'long',
                    year: 'numeric',
                  })}
                </p>
              </div>
            </div>

            {/* Print Table */}
            <table className="w-full text-xs text-left border-collapse border border-[#6C5B7B]/30 mb-6">
              <thead>
                <tr className="bg-[#355C7D]/10 text-[#355C7D] font-bold text-[10px] uppercase">
                  <th className="border border-[#6C5B7B]/30 p-2 text-center">No</th>
                  <th className="border border-[#6C5B7B]/30 p-2">NIM</th>
                  <th className="border border-[#6C5B7B]/30 p-2">Nama Mahasiswa</th>
                  <th className="border border-[#6C5B7B]/30 p-2 text-center">Kehadiran</th>
                  <th className="border border-[#6C5B7B]/30 p-2 text-center">Nilai Akhir</th>
                  <th className="border border-[#6C5B7B]/30 p-2 text-center">Huruf</th>
                  <th className="border border-[#6C5B7B]/30 p-2 text-center">Indeks</th>
                  <th className="border border-[#6C5B7B]/30 p-2 text-center">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#6C5B7B]/30 text-[#355C7D]">
                {rekapList.map((r, i) => (
                  <tr key={r.mahasiswa.id} className="text-[11px]">
                    <td className="border border-[#6C5B7B]/30 p-1.5 text-center font-mono">
                      {i + 1}
                    </td>
                    <td className="border border-[#6C5B7B]/30 p-1.5 font-mono">
                      {r.mahasiswa.nim}
                    </td>
                    <td className="border border-[#6C5B7B]/30 p-1.5 font-bold">
                      {r.mahasiswa.nama}
                    </td>
                    <td className="border border-[#6C5B7B]/30 p-1.5 text-center">
                      {r.mahasiswa.kehadiranPersen}%
                    </td>
                    <td className="border border-[#6C5B7B]/30 p-1.5 text-center font-bold">
                      {r.nilaiAkhir.toFixed(1)}
                    </td>
                    <td className="border border-[#6C5B7B]/30 p-1.5 text-center font-bold">
                      {r.hurufMutu}
                    </td>
                    <td className="border border-[#6C5B7B]/30 p-1.5 text-center font-mono">
                      {r.indeks.toFixed(1)}
                    </td>
                    <td className="border border-[#6C5B7B]/30 p-1.5 text-center font-bold">
                      {r.statusLulus}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Signature Area */}
            <div className="grid grid-cols-2 text-xs pt-4 text-center">
              <div>
                <p className="text-[#6C5B7B]">Mengetahui,</p>
                <p className="font-bold text-[#355C7D]">
                  Ketua Program Studi S1 Teknik Komputer
                </p>
                <div className="h-16"></div>
                <p className="font-bold underline text-[#355C7D]">
                  Dr. Ir. Hendra Gunawan, S.Kom., M.T.
                </p>
                <p className="text-[10px] text-[#6C5B7B]">NIP. 19780514 200501 1 002</p>
              </div>

              <div>
                <p className="text-[#6C5B7B]">
                  Bandung,{' '}
                  {new Date().toLocaleDateString('id-ID', {
                    day: 'numeric',
                    month: 'long',
                    year: 'numeric',
                  })}
                </p>
                <p className="font-bold text-[#355C7D]">Dosen Pengampu Mata Kuliah</p>
                <div className="h-16"></div>
                <p className="font-bold underline text-[#355C7D]">{dosen.nama}</p>
                <p className="text-[10px] text-[#6C5B7B]">NIP. {dosen.nip}</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default GradeSummarySection;
