import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { AcademicProvider } from './context/AcademicContext';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <AcademicProvider>
      <App />
    </AcademicProvider>
  </React.StrictMode>
);
