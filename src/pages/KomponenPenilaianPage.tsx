import React from 'react';
import { NavigationSidebarSection } from '../components/layout/SidebarNavigationSection';
import { AssessmentComponentsSection } from '../components/assessments/AssessmentComponentsSection';

export const KomponenPenilaianPage: React.FC = () => {
  return (
    <main className="flex min-h-screen flex-col items-center bg-[#F8B195]/15 p-0 sm:p-4">
      <div className="flex min-h-[calc(100vh-32px)] w-full max-w-[1600px] rounded-xl overflow-hidden bg-[#fbfafc] shadow-[0px_16px_50px_#355C7D20,0px_0px_0px_1px_#6C5B7B33]">
        <NavigationSidebarSection />
        <AssessmentComponentsSection />
      </div>
    </main>
  );
};

export default KomponenPenilaianPage;
