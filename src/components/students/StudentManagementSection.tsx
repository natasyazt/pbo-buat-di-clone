import React, { useState } from 'react';
import {
  Users,
  Search,
  Plus,
  Download,
  Upload,
  Edit2,
  Trash2,
  X,
  Check,
  FileSpreadsheet,
} from 'lucide-react';
import { useAcademic } from '../../context/AcademicContext';
import { Mahasiswa } from '../../types/academic';
import { getGradeBadgeColor, getStatusBadgeColor } from '../../utils/gradeCalculator';

export const StudentManagementSection: React.FC = () => {
  const {
    mahasiswaList,
    mataKuliahList,
    selectedMataKuliahId,
    setSelectedMataKuliahId,
    addMahasiswa,
    updateMahasiswa,
    deleteMahasiswa,
    getRekapNilaiByMataKuliah,
    setCurrentView,
  } = useAcademic();

  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState<string>('ALL');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isBatchOpen, setIsBatchOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState<Mahasiswa | null>(null);

  // Form State
  const [formData, setFormData] = useState({
    nim: '',
    nama: '',
    email: '',
    angkatan: '2023',
    prodi: 'Teknik Komputer',
    kelas: 'IF-45-01',
    kehadiranPersen: 90,
    status: 'Aktif' as Mahasiswa['status'],
    mataKuliahIds: [selectedMataKuliahId],
  });

  const [batchText, setBatchText] = useState('');

  const currentCourse =
    mataKuliahList.find((m) => m.id === selectedMataKuliahId) ||
    mataKuliahList[0];

  const currentRekap = currentCourse
    ? getRekapNilaiByMataKuliah(currentCourse.id)
    : [];

  const handleOpenAdd = () => {
    setEditingStudent(null);
    setFormData({
      nim: '',
      nama: '',
      email: '',
      angkatan: '2023',
      prodi: 'Teknik Komputer',
      kelas: currentCourse?.kelas || 'IF-45-01',
      kehadiranPersen: 95,
      status: 'Aktif',
      mataKuliahIds: [selectedMataKuliahId],
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (mhs: Mahasiswa) => {
    setEditingStudent(mhs);
    setFormData({
      nim: mhs.nim,
      nama: mhs.nama,
      email: mhs.email,
      angkatan: mhs.angkatan,
      prodi: mhs.prodi,
      kelas: mhs.kelas,
      kehadiranPersen: mhs.kehadiranPersen,
      status: mhs.status,
      mataKuliahIds: mhs.mataKuliahIds,
    });
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.nim || !formData.nama) {
      alert('NIM dan Nama Mahasiswa wajib diisi.');
      return;
    }

    if (editingStudent) {
      updateMahasiswa(editingStudent.id, formData);
    } else {
      addMahasiswa(formData);
    }
    setIsModalOpen(false);
  };

  const handleDelete = (id: string, nama: string) => {
    if (window.confirm(`Hapus mahasiswa "${nama}" beserta catatan nilainya?`)) {
      deleteMahasiswa(id);
    }
  };

  // Batch import parser
  const handleBatchImport = () => {
    const lines = batchText.trim().split('\n');
    let count = 0;
    lines.forEach((line) => {
      const parts = line.split(/[\t,;]+/);
      if (parts.length >= 2) {
        const nim = parts[0].trim();
        const nama = parts[1].trim();
        if (nim && nama) {
          addMahasiswa({
            nim,
            nama,
            email: `${nim}@student.ac.id`,
            angkatan: '2023',
            prodi: 'Teknik Komputer',
            kelas: currentCourse?.kelas || 'IF-45-01',
            kehadiranPersen: 90,
            status: 'Aktif',
            mataKuliahIds: [selectedMataKuliahId],
          });
          count++;
        }
      }
    });

    if (count > 0) {
      alert(`Berhasil mengimpor ${count} mahasiswa baru!`);
      setIsBatchOpen(false);
      setBatchText('');
    } else {
      alert('Format teks tidak valid. Gunakan format: NIM, Nama Mahasiswa per baris.');
    }
  };

  // Export to CSV
  const handleExportCSV = () => {
    const headers = ['NIM', 'Nama Mahasiswa', 'Kelas', 'Prodi', 'Angkatan', 'Kehadiran (%)', 'Status'];
    const rows = filteredStudents.map((s) => [
      `"${s.nim}"`,
      `"${s.nama}"`,
      `"${s.kelas}"`,
      `"${s.prodi}"`,
      `"${s.angkatan}"`,
      s.kehadiranPersen,
      `"${s.status}"`,
    ]);

    const csvContent =
      'data:text/csv;charset=utf-8,' +
      [headers.join(','), ...rows.map((e) => e.join(','))].join('\n');

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `Daftar_Mahasiswa_${currentCourse?.kode || 'Kelas'}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Filter students based on course, search, and status
  const studentsInCourse = mahasiswaList.filter((m) =>
    selectedMataKuliahId === 'ALL' ? true : m.mataKuliahIds.includes(selectedMataKuliahId)
  );

  const filteredStudents = studentsInCourse.filter((m) => {
    const matchesSearch =
      m.nama.toLowerCase().includes(searchQuery.toLowerCase()) ||
      m.nim.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = filterStatus === 'ALL' || m.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="flex-1 min-h-[641px] bg-[#fbfafc] text-[#355C7D] p-6 lg:p-8 overflow-y-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between pb-6 border-b border-[#6C5B7B]/20 gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-extrabold uppercase tracking-widest bg-[#355C7D] text-[#F8B195] px-2.5 py-0.5 rounded-full">
              DATA AKADEMIK
            </span>
            <span className="text-xs text-[#6C5B7B] font-medium">• Mahasiswa Bimbingan</span>
          </div>
          <h2 className="text-2xl font-serif font-bold text-[#355C7D] mt-1.5">
            Daftar & Pemantauan Mahasiswa
          </h2>
          <p className="text-xs text-[#6C5B7B] mt-0.5">
            Kelola data mahasiswa terdaftar, presensi kehadiran, serta riwayat catatan performa perkuliahan.
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={() => setIsBatchOpen(true)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-[#C06C84] bg-white text-xs font-semibold hover:bg-[#F8B195]/20 text-[#C06C84] transition-all shadow-sm"
          >
            <Upload className="w-3.5 h-3.5" />
            <span>Impor Batch</span>
          </button>
          <button
            onClick={handleExportCSV}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-[#C06C84] bg-white text-xs font-semibold hover:bg-[#F8B195]/20 text-[#C06C84] transition-all shadow-sm"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Ekspor CSV</span>
          </button>
          <button
            onClick={handleOpenAdd}
            className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg bg-[#355C7D] text-white text-xs font-semibold hover:bg-[#28465f] transition-all shadow-sm"
          >
            <Plus className="w-3.5 h-3.5 text-[#F8B195]" />
            <span>Tambah Mahasiswa</span>
          </button>
        </div>
      </div>

      {/* Course & Filter Bar */}
      <div className="my-5 p-3.5 rounded-xl bg-white border border-[#6C5B7B]/20 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-3">
        <div className="flex flex-wrap items-center gap-3">
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-[#6C5B7B]">Filter Kelas MK:</span>
            <select
              value={selectedMataKuliahId}
              onChange={(e) => setSelectedMataKuliahId(e.target.value)}
              className="bg-[#fbfafc] text-xs font-semibold text-[#355C7D] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 focus:outline-none focus:border-[#355C7D]"
            >
              <option value="ALL">Semua Kelas Mata Kuliah</option>
              {mataKuliahList.map((mk) => (
                <option key={mk.id} value={mk.id}>
                  {mk.kode} - {mk.nama} ({mk.kelas})
                </option>
              ))}
            </select>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-[#6C5B7B]">Status:</span>
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="bg-[#fbfafc] text-xs font-semibold text-[#355C7D] border border-[#6C5B7B]/30 rounded-lg px-2.5 py-1.5 focus:outline-none focus:border-[#355C7D]"
            >
              <option value="ALL">Semua Status</option>
              <option value="Aktif">Aktif</option>
              <option value="Berisiko">Berisiko (Kehadiran Rendah)</option>
              <option value="Cuti">Cuti</option>
            </select>
          </div>
        </div>

        {/* Search */}
        <div className="relative w-full md:w-64">
          <Search className="w-3.5 h-3.5 absolute left-3 top-2.5 text-[#C06C84]" />
          <input
            type="text"
            placeholder="Cari NIM atau nama..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-[#fbfafc] text-xs text-[#355C7D] border border-[#6C5B7B]/30 rounded-lg pl-8 pr-3 py-1.5 focus:outline-none focus:border-[#355C7D]"
          />
        </div>
      </div>

      {/* Table of Students */}
      <div className="bg-white rounded-xl border border-[#6C5B7B]/20 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-[#355C7D] text-white uppercase text-[10px] tracking-wider font-semibold">
                <th className="py-3 px-4 w-12 text-center">No</th>
                <th className="py-3 px-4">NIM</th>
                <th className="py-3 px-4">Nama Lengkap</th>
                <th className="py-3 px-4">Kelas & Prodi</th>
                <th className="py-3 px-4 text-center">Kehadiran</th>
                <th className="py-3 px-4 text-center">Nilai Sementara</th>
                <th className="py-3 px-4 text-center">Status</th>
                <th className="py-3 px-4 text-center">Aksi</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#f2edf4] text-[#355C7D]">
              {filteredStudents.length === 0 ? (
                <tr>
                  <td colSpan={8} className="py-12 text-center text-[#6C5B7B]">
                    <Users className="w-8 h-8 mx-auto mb-2 opacity-40" />
                    <p className="font-semibold text-xs">Tidak ada data mahasiswa ditemukan.</p>
                    <p className="text-[10px]">Coba ubah kata kunci pencarian atau filter kelas.</p>
                  </td>
                </tr>
              ) : (
                filteredStudents.map((mhs, idx) => {
                  const studentGradeInfo = currentRekap.find(
                    (r) => r.mahasiswa.id === mhs.id
                  );
                  const gradeBadge = studentGradeInfo
                    ? getGradeBadgeColor(studentGradeInfo.hurufMutu)
                    : null;

                  return (
                    <tr
                      key={mhs.id}
                      className="hover:bg-[#F8B195]/10 transition-colors group"
                    >
                      <td className="py-3 px-4 text-center font-mono text-[#6C5B7B] text-[11px]">
                        {idx + 1}
                      </td>
                      <td className="py-3 px-4 font-mono font-bold text-[#355C7D]">
                        {mhs.nim}
                      </td>
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-2.5">
                          <div className="w-7 h-7 rounded-full bg-[#F8B195] text-[#355C7D] font-bold text-[10px] flex items-center justify-center border border-[#F8B195]">
                            {mhs.nama
                              .split(' ')
                              .map((n) => n[0])
                              .slice(0, 2)
                              .join('')}
                          </div>
                          <div>
                            <div className="font-bold text-[#355C7D] text-xs">
                              {mhs.nama}
                            </div>
                            <div className="text-[10px] text-[#6C5B7B]">
                              {mhs.email}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <span className="font-semibold text-[#355C7D]">
                          {mhs.kelas}
                        </span>
                        <div className="text-[10px] text-[#6C5B7B]">
                          {mhs.prodi} • {mhs.angkatan}
                        </div>
                      </td>
                      <td className="py-3 px-4 text-center">
                        <div className="inline-flex flex-col items-center">
                          <span
                            className={`font-bold text-[11px] ${mhs.kehadiranPersen < 75
                              ? 'text-[#F67280]'
                              : 'text-[#355C7D]'
                              }`}
                          >
                            {mhs.kehadiranPersen}%
                          </span>
                          <div className="w-16 bg-[#f2edf4] h-1.5 rounded-full overflow-hidden mt-1">
                            <div
                              style={{ width: `${mhs.kehadiranPersen}%` }}
                              className={`h-full ${mhs.kehadiranPersen < 75 ? 'bg-[#F67280]' : 'bg-[#355C7D]'
                                }`}
                            ></div>
                          </div>
                        </div>
                      </td>
                      <td className="py-3 px-4 text-center">
                        {studentGradeInfo ? (
                          <div className="inline-flex items-center gap-1.5">
                            <span className="font-bold text-xs">
                              {studentGradeInfo.nilaiAkhir.toFixed(1)}
                            </span>
                            <span
                              className={`px-1.5 py-0.2 rounded text-[10px] font-extrabold border ${gradeBadge?.bg} ${gradeBadge?.text} ${gradeBadge?.border}`}
                            >
                              {studentGradeInfo.hurufMutu}
                            </span>
                          </div>
                        ) : (
                          <span className="text-[#6C5B7B] text-[10px] italic">
                            Belum Ada Nilai
                          </span>
                        )}
                      </td>
                      <td className="py-3 px-4 text-center">
                        <span
                          className={`inline-block px-2 py-0.5 rounded-full text-[10px] font-bold ${mhs.status === 'Aktif'
                            ? 'bg-[#355C7D]/15 text-[#355C7D]'
                            : mhs.status === 'Berisiko'
                              ? 'bg-[#F67280]/20 text-[#C06C84]'
                              : 'bg-gray-100 text-gray-700'
                            }`}
                        >
                          {mhs.status}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-center">
                        <div className="flex items-center justify-center gap-1">
                          <button
                            onClick={() => {
                              setSelectedMataKuliahId(mhs.mataKuliahIds[0] || selectedMataKuliahId);
                              setCurrentView('input-nilai');
                            }}
                            title="Buka Nilai Mahasiswa"
                            className="p-1 rounded hover:bg-[#f2edf4] text-[#355C7D]"
                          >
                            <FileSpreadsheet className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => handleOpenEdit(mhs)}
                            title="Edit Data Mahasiswa"
                            className="p-1 rounded hover:bg-[#f2edf4] text-[#6C5B7B]"
                          >
                            <Edit2 className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => handleDelete(mhs.id, mhs.nama)}
                            title="Hapus Mahasiswa"
                            className="p-1 rounded hover:bg-[#F67280]/20 text-[#F67280]"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        <div className="p-3 bg-[#fbfafc] border-t border-[#6C5B7B]/20 flex items-center justify-between text-xs text-[#6C5B7B]">
          <span>Total {filteredStudents.length} Mahasiswa ditampilkan</span>
          <span>Dosen Pengampu: Dra. Rina Puspita, M.Kom</span>
        </div>
      </div>

      {/* Modal Add/Edit */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-xl border border-[#6C5B7B]/30 shadow-2xl w-full max-w-lg overflow-hidden animate-in fade-in zoom-in-95">
            <div className="p-4 bg-[#355C7D] text-white flex items-center justify-between">
              <h3 className="text-sm font-serif font-bold">
                {editingStudent ? 'Edit Data Mahasiswa' : 'Tambah Mahasiswa Baru'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-[#F8B195] hover:text-white"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-5 space-y-3.5 text-xs text-[#355C7D]">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    NIM (Nomor Induk Mahasiswa) *
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. 1301213001"
                    value={formData.nim}
                    onChange={(e) => setFormData({ ...formData, nim: e.target.value })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 font-mono"
                  />
                </div>
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Kelas Terdaftar *
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. IF-45-01"
                    value={formData.kelas}
                    onChange={(e) => setFormData({ ...formData, kelas: e.target.value })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-[#6C5B7B] mb-1">
                  Nama Lengkap Mahasiswa *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Muhammad Rayhan Pratama"
                  value={formData.nama}
                  onChange={(e) => setFormData({ ...formData, nama: e.target.value })}
                  className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 font-medium"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Email Mahasiswa
                  </label>
                  <input
                    type="email"
                    placeholder="e.g. rayhan@student.ac.id"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5"
                  />
                </div>
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Angkatan
                  </label>
                  <input
                    type="text"
                    placeholder="2023"
                    value={formData.angkatan}
                    onChange={(e) => setFormData({ ...formData, angkatan: e.target.value })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Persentase Presensi Kehadiran (%)
                  </label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={formData.kehadiranPersen}
                    onChange={(e) =>
                      setFormData({ ...formData, kehadiranPersen: Number(e.target.value) })
                    }
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5"
                  />
                </div>
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Status Akademik
                  </label>
                  <select
                    value={formData.status}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        status: e.target.value as Mahasiswa['status'],
                      })
                    }
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5"
                  >
                    <option value="Aktif">Aktif</option>
                    <option value="Berisiko">Berisiko</option>
                    <option value="Cuti">Cuti</option>
                    <option value="Non-Aktif">Non-Aktif</option>
                  </select>
                </div>
              </div>

              <div className="pt-3 border-t border-[#f2edf4] flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-3.5 py-1.5 rounded-lg border border-[#6C5B7B]/30 bg-white text-xs font-semibold text-[#6C5B7B]"
                >
                  Batal
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 rounded-lg bg-[#355C7D] text-white text-xs font-semibold flex items-center gap-1.5 hover:bg-[#28465f]"
                >
                  <Check className="w-3.5 h-3.5 text-[#F8B195]" />
                  <span>Simpan Data</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Batch Import */}
      {isBatchOpen && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-xl border border-[#6C5B7B]/30 shadow-2xl w-full max-w-lg overflow-hidden animate-in fade-in zoom-in-95">
            <div className="p-4 bg-[#355C7D] text-white flex items-center justify-between">
              <h3 className="text-sm font-serif font-bold">
                Impor Batch Data Mahasiswa
              </h3>
              <button
                onClick={() => setIsBatchOpen(false)}
                className="text-[#F8B195] hover:text-white"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="p-5 space-y-3 text-xs text-[#355C7D]">
              <p className="text-[#6C5B7B]">
                Salin dan tempel daftar mahasiswa dari Excel/Word/Spreadsheet ke kotak di bawah. Satu baris per mahasiswa dengan format: <br />
                <code className="bg-[#f2edf4] px-1 py-0.5 rounded font-mono text-[11px] text-[#355C7D]">
                  NIM, Nama Mahasiswa
                </code>
              </p>

              <textarea
                rows={6}
                value={batchText}
                onChange={(e) => setBatchText(e.target.value)}
                placeholder={"1301213030, Aldi Pratama\n1301213031, Bella Safitri\n1301213032, Candra Wijaya"}
                className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg p-2.5 font-mono text-xs resize-none"
              />

              <div className="pt-2 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsBatchOpen(false)}
                  className="px-3.5 py-1.5 rounded-lg border border-[#6C5B7B]/30 bg-white text-xs font-semibold text-[#6C5B7B]"
                >
                  Batal
                </button>
                <button
                  type="button"
                  onClick={handleBatchImport}
                  className="px-4 py-1.5 rounded-lg bg-[#355C7D] text-white text-xs font-semibold hover:bg-[#28465f]"
                >
                  Proses & Impor Mahasiswa
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentManagementSection;
