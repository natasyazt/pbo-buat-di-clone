import React, { useState } from 'react';
import {
  BookOpen,
  Plus,
  Search,
  Edit2,
  Trash2,
  Users,
  Sliders,
  FileSpreadsheet,
  X,
  Check,
  Calendar,
  MapPin,
  Clock,
} from 'lucide-react';
import { useAcademic } from '../../context/AcademicContext';
import { MataKuliah } from '../../types/academic';

export const CourseManagementSection: React.FC = () => {
  const {
    mataKuliahList,
    addMataKuliah,
    updateMataKuliah,
    deleteMataKuliah,
    mahasiswaList,
    getKomponenByMataKuliah,
    setSelectedMataKuliahId,
    setCurrentView,
  } = useAcademic();

  const [searchQuery, setSearchQuery] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCourse, setEditingCourse] = useState<MataKuliah | null>(null);

  // Form State
  const [formData, setFormData] = useState({
    kode: '',
    nama: '',
    sks: 3,
    semester: 'Genap 2024/2025',
    kelas: '',
    ruang: '',
    jadwal: '',
    deskripsi: '',
  });

  const handleOpenAdd = () => {
    setEditingCourse(null);
    setFormData({
      kode: '',
      nama: '',
      sks: 3,
      semester: 'Genap 2024/2025',
      kelas: '',
      ruang: '',
      jadwal: '',
      deskripsi: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (mk: MataKuliah) => {
    setEditingCourse(mk);
    setFormData({
      kode: mk.kode,
      nama: mk.nama,
      sks: mk.sks,
      semester: mk.semester,
      kelas: mk.kelas,
      ruang: mk.ruang,
      jadwal: mk.jadwal,
      deskripsi: mk.deskripsi || '',
    });
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.kode || !formData.nama || !formData.kelas) {
      alert('Mohon lengkapi Kode, Nama Mata Kuliah, dan Kelas.');
      return;
    }

    if (editingCourse) {
      updateMataKuliah(editingCourse.id, formData);
    } else {
      addMataKuliah(formData);
    }
    setIsModalOpen(false);
  };

  const handleDelete = (id: string, name: string) => {
    if (window.confirm(`Yakin ingin menghapus mata kuliah "${name}" beserta data penilaiannya?`)) {
      deleteMataKuliah(id);
    }
  };

  const filteredCourses = mataKuliahList.filter(
    (mk) =>
      mk.nama.toLowerCase().includes(searchQuery.toLowerCase()) ||
      mk.kode.toLowerCase().includes(searchQuery.toLowerCase()) ||
      mk.kelas.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="flex-1 min-h-[641px] bg-[#fbfafc] text-[#355C7D] p-6 lg:p-8 overflow-y-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between pb-6 border-b border-[#6C5B7B]/20 gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-extrabold uppercase tracking-widest bg-[#355C7D] text-[#F8B195] px-2.5 py-0.5 rounded-full">
              KURIKULUM & KELAS
            </span>
            <span className="text-xs text-[#6C5B7B] font-medium">• Semester Genap 2024/2025</span>
          </div>
          <h2 className="text-2xl font-serif font-bold text-[#355C7D] mt-1.5">
            Manajemen Mata Kuliah Diampu
          </h2>
          <p className="text-xs text-[#6C5B7B] mt-0.5">
            Kelola daftar kelas perkuliahan, beban SKS, jadwal mengajar, dan alokasi ruang perkuliahan.
          </p>
        </div>

        <button
          onClick={handleOpenAdd}
          className="flex items-center gap-2 px-4 py-2 rounded-lg bg-[#355C7D] text-white text-xs font-semibold hover:bg-[#28465f] transition-all shadow-sm self-start md:self-auto"
        >
          <Plus className="w-4 h-4 text-[#F8B195]" />
          <span>Tambah Mata Kuliah</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="my-5 flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 absolute left-3 top-2.5 text-[#C06C84]" />
          <input
            type="text"
            placeholder="Cari kode MK, nama mata kuliah, kelas..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-white text-xs text-[#355C7D] border border-[#6C5B7B]/30 rounded-lg pl-9 pr-3 py-2 focus:outline-none focus:border-[#355C7D] shadow-sm font-medium"
          />
        </div>

        <div className="text-xs text-[#6C5B7B] self-end sm:self-auto font-medium">
          Menampilkan <span className="font-bold text-[#355C7D]">{filteredCourses.length}</span> dari {mataKuliahList.length} mata kuliah
        </div>
      </div>

      {/* Courses Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filteredCourses.map((mk) => {
          const enrolled = mahasiswaList.filter((m) =>
            m.mataKuliahIds.includes(mk.id)
          ).length;
          const komps = getKomponenByMataKuliah(mk.id);
          const totalBobot = komps.reduce((acc, k) => acc + k.bobot, 0);

          return (
            <div
              key={mk.id}
              className="bg-white rounded-xl border border-[#6C5B7B]/20 p-5 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between"
            >
              <div>
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] font-mono font-bold bg-[#355C7D] text-[#F8B195] px-2 py-0.5 rounded">
                      {mk.kode}
                    </span>
                    <span className="text-[10px] font-bold bg-[#f2edf4] text-[#6C5B7B] px-2 py-0.5 rounded border border-[#6C5B7B]/20">
                      {mk.sks} SKS
                    </span>
                  </div>
                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => handleOpenEdit(mk)}
                      title="Edit Mata Kuliah"
                      className="p-1.5 rounded-lg hover:bg-[#f2edf4] text-[#6C5B7B] hover:text-[#355C7D] transition-colors"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => handleDelete(mk.id, mk.nama)}
                      title="Hapus Mata Kuliah"
                      className="p-1.5 rounded-lg hover:bg-[#F67280]/20 text-[#F67280] hover:text-[#C06C84] transition-colors"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                <h3 className="text-base font-serif font-bold text-[#355C7D] mt-2">
                  {mk.nama}
                </h3>
                <p className="text-xs font-semibold text-[#6C5B7B] mt-0.5">
                  Kelas: <span className="text-[#355C7D]">{mk.kelas}</span> • {mk.semester}
                </p>

                {mk.deskripsi && (
                  <p className="text-[11px] text-[#6C5B7B] mt-2 line-clamp-2 italic">
                    "{mk.deskripsi}"
                  </p>
                )}

                <div className="mt-4 pt-3 border-t border-[#f2edf4] space-y-1.5 text-xs text-[#6C5B7B]">
                  <div className="flex items-center gap-2">
                    <Clock className="w-3.5 h-3.5 text-[#C06C84]" />
                    <span>{mk.jadwal}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <MapPin className="w-3.5 h-3.5 text-[#C06C84]" />
                    <span>{mk.ruang}</span>
                  </div>
                </div>
              </div>

              {/* Action Footer */}
              <div className="mt-5 pt-3 border-t border-[#f2edf4]">
                <div className="flex items-center justify-between text-[11px] text-[#6C5B7B] mb-3">
                  <span className="flex items-center gap-1 font-medium">
                    <Users className="w-3.5 h-3.5 text-[#355C7D]" />
                    {enrolled} Mahasiswa Terdaftar
                  </span>
                  <span
                    className={`font-semibold ${
                      totalBobot === 100 ? 'text-[#355C7D]' : 'text-[#F67280]'
                    }`}
                  >
                    Bobot: {totalBobot}%
                  </span>
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <button
                    onClick={() => {
                      setSelectedMataKuliahId(mk.id);
                      setCurrentView('komponen');
                    }}
                    className="flex items-center justify-center gap-1.5 py-1.5 px-2 rounded-lg border border-[#C06C84] bg-white text-xs font-semibold text-[#C06C84] hover:bg-[#F8B195]/20 transition-colors"
                  >
                    <Sliders className="w-3 h-3" />
                    <span>Atur Bobot</span>
                  </button>
                  <button
                    onClick={() => {
                      setSelectedMataKuliahId(mk.id);
                      setCurrentView('input-nilai');
                    }}
                    className="flex items-center justify-center gap-1.5 py-1.5 px-2 rounded-lg bg-[#355C7D] text-white text-xs font-semibold hover:bg-[#28465f] transition-colors shadow-xs"
                  >
                    <FileSpreadsheet className="w-3 h-3 text-[#F8B195]" />
                    <span>Input Nilai</span>
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Modal Add/Edit */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-xl border border-[#6C5B7B]/30 shadow-2xl w-full max-w-lg overflow-hidden animate-in fade-in zoom-in-95">
            <div className="p-4 bg-[#355C7D] text-white flex items-center justify-between">
              <h3 className="text-sm font-serif font-bold tracking-wide">
                {editingCourse ? 'Edit Informasi Mata Kuliah' : 'Tambah Mata Kuliah Baru'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-[#F8B195] hover:text-white"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-5 space-y-3.5 text-xs text-[#355C7D]">
              <div className="grid grid-cols-3 gap-3">
                <div className="col-span-1">
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Kode MK *
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. IF2104"
                    value={formData.kode}
                    onChange={(e) => setFormData({ ...formData, kode: e.target.value })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 font-mono uppercase focus:border-[#355C7D]"
                  />
                </div>
                <div className="col-span-1">
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    SKS *
                  </label>
                  <input
                    type="number"
                    min="1"
                    max="6"
                    required
                    value={formData.sks}
                    onChange={(e) => setFormData({ ...formData, sks: Number(e.target.value) })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 focus:border-[#355C7D]"
                  />
                </div>
                <div className="col-span-1">
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Kelas *
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. IF-45-01"
                    value={formData.kelas}
                    onChange={(e) => setFormData({ ...formData, kelas: e.target.value })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 focus:border-[#355C7D]"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-[#6C5B7B] mb-1">
                  Nama Mata Kuliah *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Pemrograman Berorientasi Objek"
                  value={formData.nama}
                  onChange={(e) => setFormData({ ...formData, nama: e.target.value })}
                  className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 font-medium focus:border-[#355C7D]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Jadwal Perkuliahan
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. Senin, 08:00 - 10:30 WIB"
                    value={formData.jadwal}
                    onChange={(e) => setFormData({ ...formData, jadwal: e.target.value })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 focus:border-[#355C7D]"
                  />
                </div>
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Ruang / Laboratorium
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. Lab Riset 302"
                    value={formData.ruang}
                    onChange={(e) => setFormData({ ...formData, ruang: e.target.value })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 focus:border-[#355C7D]"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-[#6C5B7B] mb-1">
                  Deskripsi / Capaian Pembelajaran (Opsional)
                </label>
                <textarea
                  rows={2}
                  placeholder="Ringkasan materi perkuliahan..."
                  value={formData.deskripsi}
                  onChange={(e) => setFormData({ ...formData, deskripsi: e.target.value })}
                  className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 resize-none focus:border-[#355C7D]"
                />
              </div>

              <div className="pt-3 border-t border-[#f2edf4] flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-3.5 py-1.5 rounded-lg border border-[#6C5B7B]/30 bg-white text-xs font-semibold text-[#6C5B7B] hover:bg-[#f2edf4]"
                >
                  Batal
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 rounded-lg bg-[#355C7D] text-white text-xs font-semibold hover:bg-[#28465f] flex items-center gap-1.5"
                >
                  <Check className="w-3.5 h-3.5 text-[#F8B195]" />
                  <span>Simpan Perubahan</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default CourseManagementSection;
