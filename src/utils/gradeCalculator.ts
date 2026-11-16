import { KomponenPenilaian, RekapNilaiDetail, Mahasiswa, NilaiMahasiswa } from '../types/academic';

export interface GradeScaleResult {
  huruf: 'A' | 'AB' | 'B' | 'BC' | 'C' | 'D' | 'E';
  indeks: number;
  status: 'LULUS' | 'REMIDI' | 'TIDAK LULUS';
}

export function hitungSkalaNilai(nilaiAkhir: number): GradeScaleResult {
  const rounded = Math.round(nilaiAkhir * 10) / 10;
  if (rounded >= 80.0) {
    return { huruf: 'A', indeks: 4.0, status: 'LULUS' };
  } else if (rounded >= 75.0) {
    return { huruf: 'AB', indeks: 3.5, status: 'LULUS' };
  } else if (rounded >= 70.0) {
    return { huruf: 'B', indeks: 3.0, status: 'LULUS' };
  } else if (rounded >= 65.0) {
    return { huruf: 'BC', indeks: 2.5, status: 'LULUS' };
  } else if (rounded >= 60.0) {
    return { huruf: 'C', indeks: 2.0, status: 'LULUS' };
  } else if (rounded >= 50.0) {
    return { huruf: 'D', indeks: 1.0, status: 'REMIDI' };
  } else {
    return { huruf: 'E', indeks: 0.0, status: 'TIDAK LULUS' };
  }
}

export function hitungNilaiMahasiswa(
  mahasiswa: Mahasiswa,
  komponenList: KomponenPenilaian[],
  nilaiRecord?: NilaiMahasiswa
): RekapNilaiDetail {
  const scores = nilaiRecord?.nilaiKomponen || {};
  let totalBobot = 0;
  let totalNilaiTertimbang = 0;

  komponenList.forEach((komp) => {
    totalBobot += komp.bobot;
    const score = scores[komp.id] ?? 0;
    totalNilaiTertimbang += score * (komp.bobot / 100);
  });

  const nilaiAkhir = totalBobot > 0 
    ? (totalNilaiTertimbang / (totalBobot / 100))
    : 0;
  
  const roundedAkhir = Math.round(nilaiAkhir * 100) / 100;
  const grade = hitungSkalaNilai(roundedAkhir);

  return {
    mahasiswa,
    nilaiKomponen: scores,
    nilaiAkhir: roundedAkhir,
    hurufMutu: grade.huruf,
    indeks: grade.indeks,
    statusLulus: grade.status,
    catatanDosen: nilaiRecord?.catatanDosen || '',
  };
}

export function getGradeBadgeColor(grade: string): { bg: string; text: string; border: string } {
  switch (grade) {
    case 'A':
      return { bg: 'bg-[#355C7D]/15', text: 'text-[#355C7D]', border: 'border-[#355C7D]/40' };
    case 'AB':
      return { bg: 'bg-[#6C5B7B]/15', text: 'text-[#6C5B7B]', border: 'border-[#6C5B7B]/40' };
    case 'B':
      return { bg: 'bg-[#355C7D]/10', text: 'text-[#355C7D]', border: 'border-[#355C7D]/30' };
    case 'BC':
      return { bg: 'bg-[#6C5B7B]/10', text: 'text-[#6C5B7B]', border: 'border-[#6C5B7B]/30' };
    case 'C':
      return { bg: 'bg-[#F8B195]/30', text: 'text-[#9c5b41]', border: 'border-[#F8B195]' };
    case 'D':
      return { bg: 'bg-[#F67280]/20', text: 'text-[#F67280]', border: 'border-[#F67280]/50' };
    case 'E':
    default:
      return { bg: 'bg-[#C06C84]/20', text: 'text-[#C06C84]', border: 'border-[#C06C84]/50' };
  }
}

export function getStatusBadgeColor(status: 'LULUS' | 'REMIDI' | 'TIDAK LULUS'): { bg: string; text: string } {
  switch (status) {
    case 'LULUS':
      return { bg: 'bg-[#355C7D]/15', text: 'text-[#355C7D]' };
    case 'REMIDI':
      return { bg: 'bg-[#F67280]/20', text: 'text-[#F67280]' };
    case 'TIDAK LULUS':
      return { bg: 'bg-[#C06C84]/20', text: 'text-[#C06C84]' };
  }
}
