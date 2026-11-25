import React, { useState } from 'react';
import {
  Mail,
  Lock,
  Eye,
  EyeOff,
  ArrowRight,
  ShieldCheck,
  CheckCircle2,
  GraduationCap,
  Sparkles,
  HelpCircle,
} from 'lucide-react';
import { useAcademic } from '../../context/AcademicContext';

export const LoginSignup: React.FC = () => {
  const { login, dosen } = useAcademic();
  const [email, setEmail] = useState('oky.nurhayati@sipena.ac.id');
  const [password, setPassword] = useState('password123');
  const [rememberMe, setRememberMe] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showHelpModal, setShowHelpModal] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage('');
    setIsSubmitting(true);

    setTimeout(() => {
      if (!email.includes('@')) {
        setErrorMessage('Format email institusi tidak valid.');
        setIsSubmitting(false);
        return;
      }
      if (password.length < 6) {
        setErrorMessage('Kata sandi minimal 6 karakter.');
        setIsSubmitting(false);
        return;
      }

      const success = login(email, password);
      if (!success) {
        setErrorMessage('Email atau kata sandi tidak sesuai.');
      }
      setIsSubmitting(false);
    }, 400);
  };

  const handleQuickLogin = () => {
    setEmail(dosen.email);
    setPassword('password123');
    login(dosen.email, 'password123');
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center relative bg-[#F8B195]/20 p-4 sm:p-6 select-none font-sans">
      <div className="flex w-full max-w-[1080px] min-h-[640px] rounded-xl overflow-hidden bg-white shadow-[0px_20px_60px_#355C7D26,0px_0px_0px_1px_#6C5B7B33]">
        <div className="grid w-full grid-cols-1 lg:grid-cols-[540px_1fr]">
          {/* Left Column: Brand & Academic Presentation */}
          <section
            aria-labelledby="brand-title"
            className="relative flex flex-col justify-between bg-gradient-to-br from-[#355C7D] via-[#4d5c7c] to-[#6C5B7B] px-8 py-8 lg:px-10 lg:py-10 text-white"
          >
            <div>
              {/* Brand Header */}
              <div className="flex items-center gap-3">
                <div
                  aria-hidden="true"
                  className="w-[44px] h-[44px] rounded-[10px] bg-[#F8B195] text-[#355C7D] font-serif font-bold text-2xl flex items-center justify-center shadow-md"
                >
                  S
                </div>
                <div>
                  <h1
                    id="brand-title"
                    className="text-[22px] font-serif font-bold tracking-[0.8px] text-white leading-tight"
                  >
                    LECTURA
                  </h1>
                  <p className="text-[10px] text-[#F8B195] tracking-wide font-medium">
                    Sistem Penilaian Akademik
                  </p>
                </div>
              </div>

              {/* Hero */}
              <div className="mt-10">
                <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#28465f]/80 border border-[#F8B195]/40 text-[#F8B195] text-[9px] font-extrabold uppercase tracking-[1.2px]">
                  <GraduationCap className="w-3.5 h-3.5 text-[#F67280]" />
                  <span>PORTAL AKADEMIK DOSEN</span>
                </div>

                <h2 className="mt-4 text-3xl lg:text-4xl font-serif font-bold text-white leading-[1.2] tracking-tight">
                  Penilaian akademik dalam satu ruang kerja.
                </h2>

                <p className="mt-3 text-xs text-[#e8f1f8] leading-relaxed max-w-[440px]">
                  Kelola mahasiswa, komponen penilaian, pembobotan evaluasi, dan
                  rekap nilai akhir secara terstruktur, konsisten, dan transparan.
                </p>
              </div>

              {/* Highlights */}
              <div className="mt-8 space-y-3">
                <div className="p-3.5 rounded-lg bg-[#28465f]/60 border border-[#F8B195]/30 flex items-center gap-3 shadow-sm backdrop-blur-sm">
                  <div className="w-9 h-9 rounded-lg bg-[#6C5B7B] flex items-center justify-center text-[#F8B195] shadow-xs">
                    <ShieldCheck className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="text-xs font-bold text-white">
                      Kalkulasi Otomatis Terintegrasi
                    </div>
                    <div className="text-[10px] text-[#d6e5f2]">
                      Nilai akhir dan huruf mutu (A s.d. E) dihitung secara instan.
                    </div>
                  </div>
                </div>

                <div className="p-3.5 rounded-lg bg-[#28465f]/60 border border-[#F8B195]/30 flex items-center gap-3 shadow-sm backdrop-blur-sm">
                  <div className="w-9 h-9 rounded-lg bg-[#C06C84] flex items-center justify-center text-white shadow-xs">
                    <CheckCircle2 className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="text-xs font-bold text-white">
                      Ekspor & Cetak Transkrip Resmi
                    </div>
                    <div className="text-[10px] text-[#d6e5f2]">
                      Dukung ekspor format CSV/Excel dan lembar cetak tandatangan BKD.
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Bottom Footer */}
            <div className="pt-6 border-t border-white/20 flex items-center justify-between text-[10px] text-[#d0e2f2]">
              <span>© 2025 LECTURA · SIPENA</span>
              <span>Semester Genap 2024/2025</span>
            </div>
          </section>

          {/* Right Column: Form */}
          <section className="flex flex-col justify-center px-8 py-10 lg:px-12 bg-white text-[#355C7D]">
            <div className="w-full max-w-[400px] mx-auto">
              <div className="mb-6">
                <div className="text-[9px] font-extrabold uppercase tracking-[1.28px] text-[#C06C84]">
                  SELAMAT DATANG
                </div>
                <h2 className="text-2xl lg:text-3xl font-serif font-bold text-[#355C7D] mt-1">
                  Selamat Datang di LECTURA!
                </h2>
                <p className="text-xs text-[#6C5B7B] mt-1">
                  Masukkan email institusi dan kata sandi Anda untuk melanjutkan.
                </p>
              </div>

              {/* Error Alert */}
              {errorMessage && (
                <div className="mb-4 p-2.5 rounded-lg bg-[#F67280]/15 border border-[#F67280] text-[#C06C84] text-xs flex items-center gap-2 font-medium">
                  <span>{errorMessage}</span>
                </div>
              )}

              {/* Form */}
              <form onSubmit={handleSubmit} className="space-y-4">
                {/* Email Field */}
                <div>
                  <label
                    htmlFor="institution-email"
                    className="block text-[10px] font-bold uppercase tracking-wider text-[#6C5B7B] mb-1.5"
                  >
                    Email Institusi
                  </label>
                  <div className="relative flex items-center">
                    <Mail className="w-4 h-4 text-[#C06C84] absolute left-3 pointer-events-none" />
                    <input
                      id="institution-email"
                      name="email"
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                      placeholder="nama@sipena.ac.id"
                      className="w-full rounded-lg border border-[#355C7D]/30 bg-[#fbfbfe] pl-9 pr-3 py-2 text-xs text-[#355C7D] focus:outline-none focus:border-[#355C7D] focus:ring-1 focus:ring-[#355C7D] shadow-sm font-medium"
                    />
                  </div>
                </div>

                {/* Password Field */}
                <div>
                  <label
                    htmlFor="password"
                    className="block text-[10px] font-bold uppercase tracking-wider text-[#6C5B7B] mb-1.5"
                  >
                    Kata Sandi
                  </label>
                  <div className="relative flex items-center">
                    <Lock className="w-4 h-4 text-[#C06C84] absolute left-3 pointer-events-none" />
                    <input
                      id="password"
                      name="password"
                      type={showPassword ? 'text' : 'password'}
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                      minLength={6}
                      placeholder="Minimal 6 karakter"
                      className="w-full rounded-lg border border-[#355C7D]/30 bg-[#fbfbfe] pl-9 pr-10 py-2 text-xs text-[#355C7D] focus:outline-none focus:border-[#355C7D] focus:ring-1 focus:ring-[#355C7D] shadow-sm font-medium"
                    />
                    <button
                      type="button"
                      aria-label={showPassword ? 'Sembunyikan kata sandi' : 'Tampilkan kata sandi'}
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 text-[#6C5B7B] hover:text-[#355C7D]"
                    >
                      {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                </div>

                {/* Remember & Forgot */}
                <div className="flex items-center justify-between text-xs pt-1">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={rememberMe}
                      onChange={(e) => setRememberMe(e.target.checked)}
                      className="rounded border-[#C06C84] text-[#355C7D] focus:ring-0"
                    />
                    <span className="text-[11px] text-[#6C5B7B]">Ingat saya</span>
                  </label>

                  <button
                    type="button"
                    onClick={() =>
                      alert('Untuk reset kata sandi dosen, hubungi unit BAPENDIK / IT Administrator di admin@sipena.ac.id.')
                    }
                    className="text-[11px] font-bold text-[#C06C84] hover:underline"
                  >
                    Lupa kata sandi?
                  </button>
                </div>

                {/* Submit */}
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="w-full flex items-center justify-center gap-2 py-2.5 px-4 rounded-lg bg-[#355C7D] hover:bg-[#28465f] text-white text-xs font-bold transition-all shadow-md mt-2 disabled:opacity-75"
                >
                  <span>{isSubmitting ? 'Memproses...' : 'Masuk ke Dashboard'}</span>
                  <ArrowRight className="w-4 h-4 text-[#F8B195]" />
                </button>
              </form>

              {/* Demo Login Button */}
              <div className="mt-5 p-3 rounded-lg bg-[#F8B195]/20 border border-[#F8B195] flex items-center justify-between">
                <div>
                  <div className="text-[10px] font-bold text-[#355C7D] flex items-center gap-1">
                    <Sparkles className="w-3.5 h-3.5 text-[#F67280]" />
                    <span>Akun Pengujian Dosen</span>
                  </div>
                  <div className="text-[9px] text-[#6C5B7B]">
                    Dra. Rina Puspita, M.Kom. (Teknik Komputer)
                  </div>
                </div>
                <button
                  type="button"
                  onClick={handleQuickLogin}
                  className="px-3 py-1.5 rounded-lg bg-[#C06C84] hover:bg-[#a85b71] text-[10px] font-bold text-white shadow-xs transition-colors"
                >
                  Masuk Cepat
                </button>
              </div>

              {/* Register & Support */}
              <div className="mt-6 pt-4 border-t border-[#f0ebf3] text-center space-y-2">
                <p className="text-[11px] text-[#6C5B7B]">
                  Belum memiliki akun dosen?{' '}
                  <button
                    type="button"
                    onClick={() =>
                      alert('Pendaftaran akun dosen baru dilakukan oleh bagian Akademik & Kepegawaian Kampus.')
                    }
                    className="font-bold text-[#C06C84] hover:underline"
                  >
                    Daftar sekarang
                  </button>
                </p>

                <p className="text-[10px] text-[#938ba0]">
                  Mengalami kendala?{' '}
                  <button
                    type="button"
                    onClick={() => setShowHelpModal(true)}
                    className="font-bold text-[#6C5B7B] hover:underline"
                  >
                    Hubungi administrator
                  </button>
                </p>
              </div>
            </div>
          </section>
        </div>
      </div>

      {/* Help Modal */}
      {showHelpModal && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-xl p-6 max-w-sm w-full shadow-2xl border border-[#C06C84]/30 text-xs text-[#355C7D]">
            <h3 className="font-bold text-sm text-[#355C7D] mb-2 flex items-center gap-1.5">
              <HelpCircle className="w-4 h-4 text-[#C06C84]" />
              Bantuan & Layanan Administrator
            </h3>
            <p className="text-[#6C5B7B] mb-3 leading-relaxed">
              Jika Anda mengalami kendala saat masuk ke portal LECTURA, silakan hubungi tim IT Helpdesk:
            </p>
            <div className="space-y-1.5 bg-[#F8B195]/15 p-3 rounded-lg font-mono text-[11px] text-[#355C7D] border border-[#F8B195]/40">
              <div>Email: it.support@sipena.ac.id</div>
              <div>WhatsApp: +62 812-3456-7890</div>
              <div>Gedung Rektorat Lt. 2, Ruang IT</div>
            </div>
            <button
              onClick={() => setShowHelpModal(false)}
              className="mt-4 w-full py-2 rounded-lg bg-[#355C7D] text-white font-bold hover:bg-[#28465f]"
            >
              Tutup
            </button>
          </div>
        </div>
      )}
    </main>
  );
};

export default LoginSignup;
