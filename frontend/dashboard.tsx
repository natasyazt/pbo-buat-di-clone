import React from "react";
import { AcademicDashboardSection } from "./AcademicDashboardSection";
import { SidebarNavigationSection } from "./SidebarNavigationSection";

export const Dashboard: React.FC = () => {
  return (
    <main className="flex min-h-screen flex-col items-start bg-[#d8d8d6]">
      <div className="flex min-h-[641px] w-full flex-1 flex-col items-start bg-[linear-gradient(90deg,rgba(0,0,0,0.02)_0%,rgba(0,0,0,0)_0%),linear-gradient(180deg,rgba(0,0,0,0.02)_0%,rgba(0,0,0,0)_0%),linear-gradient(0deg,rgba(216,216,214,1)_0%,rgba(216,216,214,1)_100%)] p-0 sm:p-4">
        <div className="flex min-h-[calc(100vh-32px)] w-full max-w-[1600px] items-stretch overflow-hidden bg-[#f4f4f2] shadow-[0px_12px_50px_#0000001a,0px_0px_0px_1px_#b8b8b5]">
          <SidebarNavigationSection />
          <AcademicDashboardSection />
        </div>
      </div>
    </main>
  );
};

export default Dashboard;
