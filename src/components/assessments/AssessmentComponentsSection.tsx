import React, { useState } from 'react';
import {
  Sliders,
  Plus,
  Edit2,
  Trash2,
  CheckCircle2,
  AlertTriangle,
  RotateCcw,
  BookOpen,
  Info,
  X,
  Check,
  Percent,
} from 'lucide-react';
import { useAcademic } from '../../context/AcademicContext';
import { KomponenPenilaian } from '../../types/academic';

export const AssessmentComponentsSection: React.FC = () => {
  const {
    mataKuliahList,
    selectedMataKuliahId,
    setSelectedMataKuliahId,
    komponenList,
    addKomponen,
    updateKomponen,
    deleteKomponen,
  } = useAcademic();

  const currentCourse =
    mataKuliahList.find((mk) => mk.id === selectedMataKuliahId) ||
    mataKuliahList[0];

  const currentKomponen = currentCourse
    ? komponenList.filter((k) => k.mataKuliahId === currentCourse.id)
    : [];

  const totalBobot = currentKomponen.reduce((acc, k) => acc + k.bobot, 0);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingKomponen, setEditingKomponen] = useState<KomponenPenilaian | null>(null);
  const [formData, setFormData] = useState({
    nama: '',
    bobot: 20,
    kkm: 60,
    keterangan: '',
  });

  const handleOpenAdd = () => {
    setEditingKomponen(null);
    const sisaBobot = Math.max(0, 100 - totalBobot);
    setFormData({
      nama: '',
      bobot: sisaBobot > 0 ? sisaBobot : 15,
      kkm: 60,
      keterangan: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (k: KomponenPenilaian) => {
    setEditingKomponen(k);
    setFormData({
      nama: k.nama,
      bobot: k.bobot,
      kkm: k.kkm,
      keterangan: k.keterangan || '',
    });
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.nama) {
      alert('Nama komponen wajib diisi.');
      return;
    }

    if (editingKomponen) {
      updateKomponen(editingKomponen.id, formData);
    } else {
      addKomponen({
        mataKuliahId: currentCourse.id,
        nama: formData.nama,
        bobot: Number(formData.bobot),
        kkm: Number(formData.kkm),
        keterangan: formData.keterangan,
      });
    }
    setIsModalOpen(false);
  };

  const handleDelete = (id: string, name: string) => {
    if (window.confirm(`Hapus komponen "${name}"? Nilai mahasiswa pada komponen ini akan dibersihkan.`)) {
      deleteKomponen(id);
    }
  };

  // Preset templates
  const applyPresetTheory = () => {
    if (!currentCourse) return;
    if (
      !window.confirm(
        'Ganti komponen dengan template standar: Tugas (20%), Kuis (10%), UTS (35%), UAS (35%)?'
      )
    )
      return;

    currentKomponen.forEach((k) => deleteKomponen(k.id));

    addKomponen({ mataKuliahId: currentCourse.id, nama: 'Tugas Mandiri', bobot: 20, kkm: 60 });
    addKomponen({ mataKuliahId: currentCourse.id, nama: 'Kuis Teori', bobot: 10, kkm: 60 });
    addKomponen({ mataKuliahId: currentCourse.id, nama: 'UTS (Ujian Tengah Semester)', bobot: 35, kkm: 60 });
    addKomponen({ mataKuliahId: currentCourse.id, nama: 'UAS (Ujian Akhir Semester)', bobot: 35, kkm: 60 });
  };

  const applyPresetLab = () => {
    if (!currentCourse) return;
    if (
      !window.confirm(
        'Ganti komponen dengan template praktikum: Tugas (15%), Praktikum (25%), UTS (25%), Proyek Akhir (20%), UAS (15%)?'
      )
    )
      return;

    currentKomponen.forEach((k) => deleteKomponen(k.id));

    addKomponen({ mataKuliahId: currentCourse.id, nama: 'Tugas & Latihan', bobot: 15, kkm: 60 });
    addKomponen({ mataKuliahId: currentCourse.id, nama: 'Praktikum & Modul Lab', bobot: 25, kkm: 65 });
    addKomponen({ mataKuliahId: currentCourse.id, nama: 'UTS Evaluasi Coding', bobot: 25, kkm: 60 });
    addKomponen({ mataKuliahId: currentCourse.id, nama: 'Proyek Akhir Aplikasi', bobot: 20, kkm: 65 });
    addKomponen({ mataKuliahId: currentCourse.id, nama: 'UAS Komprehensif', bobot: 15, kkm: 60 });
  };

  return (
    <div className="flex-1 min-h-[641px] bg-[#fbfafc] text-[#355C7D] p-6 lg:p-8 overflow-y-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between pb-6 border-b border-[#6C5B7B]/20 gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-extrabold uppercase tracking-widest bg-[#355C7D] text-[#F8B195] px-2.5 py-0.5 rounded-full">
              SKEMA EVALUASI
            </span>
            <span className="text-xs text-[#6C5B7B] font-medium">• Konfigurasi Bobot</span>
          </div>
          <h2 className="text-2xl font-serif font-bold text-[#355C7D] mt-1.5">
            Komponen & Bobot Penilaian
          </h2>
          <p className="text-xs text-[#6C5B7B] mt-0.5">
            Atur parameter penilaian, persentase bobot tiap komponen, serta ambang batas KKM kelulusan mahasiswa.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={handleOpenAdd}
            className="flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-[#355C7D] text-white text-xs font-semibold hover:bg-[#28465f] transition-all shadow-sm"
          >
            <Plus className="w-4 h-4 text-[#F8B195]" />
            <span>Tambah Komponen</span>
          </button>
        </div>
      </div>

      {/* Course Context & Weight Status Banner */}
      <div className="my-5 p-4 rounded-xl bg-white border border-[#6C5B7B]/20 shadow-sm">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-lg bg-[#355C7D] text-[#F8B195] flex items-center justify-center font-bold text-xs shadow-xs">
              <Sliders className="w-4 h-4" />
            </div>
            <div>
              <span className="text-[10px] uppercase font-bold text-[#C06C84] tracking-wider">
                Mata Kuliah Yang Dikonfigurasi
              </span>
              <div className="flex items-center gap-2 mt-0.5">
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
            </div>
          </div>

          {/* Bobot 100% Status Badge */}
          <div className="flex items-center gap-4">
            <div className="text-right">
              <span className="text-[10px] text-[#6C5B7B] font-semibold block">
                Total Akumulasi Bobot:
              </span>
              <div className="flex items-center gap-1.5 justify-end">
                {totalBobot === 100 ? (
                  <CheckCircle2 className="w-4 h-4 text-[#355C7D]" />
                ) : (
                  <AlertTriangle className="w-4 h-4 text-[#F67280]" />
                )}
                <span
                  className={`text-lg font-serif font-bold ${
                    totalBobot === 100
                      ? 'text-[#355C7D]'
                      : totalBobot > 100
                      ? 'text-[#C06C84]'
                      : 'text-[#F67280]'
                  }`}
                >
                  {totalBobot}%
                </span>
                <span className="text-[11px] text-[#6C5B7B]">/ 100%</span>
              </div>
            </div>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="mt-3 w-full bg-[#f2edf4] h-2.5 rounded-full overflow-hidden">
          <div
            style={{ width: `${Math.min(100, totalBobot)}%` }}
            className={`h-full transition-all duration-300 ${
              totalBobot === 100
                ? 'bg-[#355C7D]'
                : totalBobot > 100
                ? 'bg-[#C06C84]'
                : 'bg-[#F67280]'
            }`}
          ></div>
        </div>

        {/* Feedback Alert if not 100% */}
        {totalBobot !== 100 && (
          <div
            className={`mt-3 p-2.5 rounded-lg text-xs flex items-center justify-between ${
              totalBobot < 100
                ? 'bg-[#F8B195]/20 text-[#C06C84] border border-[#F8B195]'
                : 'bg-[#F67280]/15 text-[#C06C84] border border-[#F67280]'
            }`}
          >
            <div className="flex items-center gap-2 font-medium">
              <AlertTriangle className="w-4 h-4 flex-shrink-0 text-[#F67280]" />
              <span>
                {totalBobot < 100
                  ? `Total bobot masih kurang ${100 - totalBobot}%. Tambahkan komponen atau sesuaikan persentase hingga genap 100%.`
                  : `Total bobot melebihi batas 100% sebesar ${totalBobot - 100}%. Kurangi bobot salah satu komponen.`}
              </span>
            </div>
          </div>
        )}
      </div>

      {/* Preset Quick Actions */}
      <div className="flex flex-wrap items-center gap-2 mb-5">
        <span className="text-xs font-bold text-[#6C5B7B]">Template Bobot Cepat:</span>
        <button
          onClick={applyPresetLab}
          className="text-[11px] font-semibold px-3 py-1 rounded-lg border border-[#C06C84] bg-white hover:bg-[#F8B195]/20 text-[#C06C84] transition-colors"
        >
          Format Praktikum & Lab (15%-25%-25%-20%-15%)
        </button>
        <button
          onClick={applyPresetTheory}
          className="text-[11px] font-semibold px-3 py-1 rounded-lg border border-[#C06C84] bg-white hover:bg-[#F8B195]/20 text-[#C06C84] transition-colors"
        >
          Format Teori Reguler (20%-10%-35%-35%)
        </button>
      </div>

      {/* Components List & Scale Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left: Components Cards */}
        <div className="lg:col-span-2 space-y-3">
          {currentKomponen.length === 0 ? (
            <div className="bg-white p-8 rounded-xl border border-[#6C5B7B]/20 text-center text-[#6C5B7B]">
              <Sliders className="w-8 h-8 mx-auto mb-2 opacity-40 text-[#355C7D]" />
              <p className="font-semibold text-xs text-[#355C7D]">
                Belum ada komponen penilaian untuk mata kuliah ini.
              </p>
              <p className="text-[10px] mt-0.5">
                Klik tombol "Tambah Komponen" atau pilih template bobot cepat di atas.
              </p>
            </div>
          ) : (
            currentKomponen.map((k) => (
              <div
                key={k.id}
                className="bg-white p-4 rounded-xl border border-[#6C5B7B]/20 shadow-sm flex items-center justify-between hover:border-[#C06C84] transition-all"
              >
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 rounded-xl bg-[#F8B195]/25 border border-[#F8B195] flex flex-col items-center justify-center font-bold">
                    <span className="text-base font-serif text-[#355C7D] leading-none">
                      {k.bobot}%
                    </span>
                    <span className="text-[8px] text-[#C06C84] uppercase tracking-wider mt-0.5 font-bold">
                      Bobot
                    </span>
                  </div>

                  <div>
                    <h4 className="text-sm font-bold text-[#355C7D]">{k.nama}</h4>
                    <div className="flex items-center gap-3 mt-1 text-[11px] text-[#6C5B7B]">
                      <span className="font-semibold text-[#6C5B7B]">
                        KKM Minimal: <span className="text-[#355C7D] font-bold">{k.kkm}</span>
                      </span>
                      {k.keterangan && (
                        <span>• {k.keterangan}</span>
                      )}
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-1.5">
                  <button
                    onClick={() => handleOpenEdit(k)}
                    className="p-1.5 rounded-lg hover:bg-[#f2edf4] text-[#6C5B7B] hover:text-[#355C7D]"
                    title="Edit Komponen"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => handleDelete(k.id, k.nama)}
                    className="p-1.5 rounded-lg hover:bg-[#F67280]/20 text-[#F67280]"
                    title="Hapus Komponen"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Right: Institutional Grading Scale */}
        <div className="bg-white p-5 rounded-xl border border-[#6C5B7B]/20 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center gap-2 pb-3 border-b border-[#f2edf4]">
              <Info className="w-4 h-4 text-[#C06C84]" />
              <h3 className="text-sm font-serif font-bold text-[#355C7D]">
                Standar Skala Nilai Mutu
              </h3>
            </div>

            <p className="text-[11px] text-[#6C5B7B] mt-2 mb-3">
              Rentang konversi nilai akhir ke huruf mutu dan bobot indeks SKS berdasarkan pedoman akademik institusi:
            </p>

            <table className="w-full text-xs text-left border-collapse">
              <thead>
                <tr className="bg-[#355C7D] text-white text-[10px] font-bold uppercase">
                  <th className="py-2 px-2.5 rounded-l-md">Huruf</th>
                  <th className="py-2 px-2.5">Rentang</th>
                  <th className="py-2 px-2.5 text-center">Indeks</th>
                  <th className="py-2 px-2.5 text-center rounded-r-md">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#f2edf4] text-[11px]">
                <tr>
                  <td className="py-2 px-2.5 font-bold text-[#355C7D]">A</td>
                  <td className="py-2 px-2.5">80.00 – 100.00</td>
                  <td className="py-2 px-2.5 text-center font-mono">4.0</td>
                  <td className="py-2 px-2.5 text-center font-bold text-[#355C7D]">LULUS</td>
                </tr>
                <tr>
                  <td className="py-2 px-2.5 font-bold text-[#355C7D]">AB</td>
                  <td className="py-2 px-2.5">75.00 – 79.99</td>
                  <td className="py-2 px-2.5 text-center font-mono">3.5</td>
                  <td className="py-2 px-2.5 text-center font-bold text-[#355C7D]">LULUS</td>
                </tr>
                <tr>
                  <td className="py-2 px-2.5 font-bold text-[#6C5B7B]">B</td>
                  <td className="py-2 px-2.5">70.00 – 74.99</td>
                  <td className="py-2 px-2.5 text-center font-mono">3.0</td>
                  <td className="py-2 px-2.5 text-center font-bold text-[#355C7D]">LULUS</td>
                </tr>
                <tr>
                  <td className="py-2 px-2.5 font-bold text-[#6C5B7B]">BC</td>
                  <td className="py-2 px-2.5">65.00 – 69.99</td>
                  <td className="py-2 px-2.5 text-center font-mono">2.5</td>
                  <td className="py-2 px-2.5 text-center font-bold text-[#355C7D]">LULUS</td>
                </tr>
                <tr>
                  <td className="py-2 px-2.5 font-bold text-[#9c5b41]">C</td>
                  <td className="py-2 px-2.5">60.00 – 64.99</td>
                  <td className="py-2 px-2.5 text-center font-mono">2.0</td>
                  <td className="py-2 px-2.5 text-center font-bold text-[#355C7D]">LULUS</td>
                </tr>
                <tr>
                  <td className="py-2 px-2.5 font-bold text-[#F67280]">D</td>
                  <td className="py-2 px-2.5">50.00 – 59.99</td>
                  <td className="py-2 px-2.5 text-center font-mono">1.0</td>
                  <td className="py-2 px-2.5 text-center font-bold text-[#F67280]">REMIDI</td>
                </tr>
                <tr>
                  <td className="py-2 px-2.5 font-bold text-[#C06C84]">E</td>
                  <td className="py-2 px-2.5">&lt; 50.00</td>
                  <td className="py-2 px-2.5 text-center font-mono">0.0</td>
                  <td className="py-2 px-2.5 text-center font-bold text-[#C06C84]">TIDAK LULUS</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div className="mt-4 pt-3 border-t border-[#f2edf4] text-[10px] text-[#6C5B7B]">
            Pedoman Standar Penilaian SIPENA © 2025
          </div>
        </div>
      </div>

      {/* Modal Add/Edit */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-xl border border-[#6C5B7B]/30 shadow-2xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in-95">
            <div className="p-4 bg-[#355C7D] text-white flex items-center justify-between">
              <h3 className="text-sm font-serif font-bold">
                {editingKomponen ? 'Edit Parameter Komponen' : 'Tambah Komponen Penilaian'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-[#F8B195] hover:text-white"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-5 space-y-3.5 text-xs text-[#355C7D]">
              <div>
                <label className="block font-bold text-[#6C5B7B] mb-1">
                  Nama Komponen Penilaian *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Praktikum Modul PBO"
                  value={formData.nama}
                  onChange={(e) => setFormData({ ...formData, nama: e.target.value })}
                  className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 font-medium"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Persentase Bobot (%) *
                  </label>
                  <input
                    type="number"
                    min="1"
                    max="100"
                    required
                    value={formData.bobot}
                    onChange={(e) => setFormData({ ...formData, bobot: Number(e.target.value) })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 font-mono"
                  />
                </div>
                <div>
                  <label className="block font-bold text-[#6C5B7B] mb-1">
                    Ambang Batas KKM *
                  </label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    required
                    value={formData.kkm}
                    onChange={(e) => setFormData({ ...formData, kkm: Number(e.target.value) })}
                    className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5 font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-[#6C5B7B] mb-1">
                  Keterangan / Rincian Penilaian (Opsional)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Pengerjaan studi kasus OOP di lab"
                  value={formData.keterangan}
                  onChange={(e) => setFormData({ ...formData, keterangan: e.target.value })}
                  className="w-full bg-[#fbfafc] border border-[#6C5B7B]/30 rounded-lg px-3 py-1.5"
                />
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
                  <span>Simpan Komponen</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AssessmentComponentsSection;
