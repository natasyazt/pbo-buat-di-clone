import React from 'react';
import { useAcademic } from './context/AcademicContext';
import { LoginSignup } from './components/auth/LoginSignup';
import { DashboardPage } from './pages/DashboardPage';
import { MataKuliahPage } from './pages/MataKuliahPage';
import { MahasiswaPage } from './pages/MahasiswaPage';
import { KomponenPenilaianPage } from './pages/KomponenPenilaianPage';
import { InputNilaiPage } from './pages/InputNilaiPage';

export const AppContent: React.FC = () => {
  const { isLoggedIn, currentView } = useAcademic();

  if (!isLoggedIn) {
    return <LoginSignup />;
  }

  switch (currentView) {
    case 'dashboard':
      return <DashboardPage />;
    case 'matakuliah':
      return <MataKuliahPage />;
    case 'mahasiswa':
      return <MahasiswaPage />;
    case 'komponen':
      return <KomponenPenilaianPage />;
    case 'input-nilai':
      return <InputNilaiPage />;
    default:
      return <DashboardPage />;
  }
};

export const App: React.FC = () => {
  return <AppContent />;
};

export default App;
