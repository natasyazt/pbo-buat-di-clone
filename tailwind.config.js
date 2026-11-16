/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
    "./frontend/**/*.{js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        palette: {
          peach: '#F8B195',
          coral: '#F67280',
          mauve: '#C06C84',
          purple: '#6C5B7B',
          ocean: '#355C7D',
          // Tints & Shades
          peachLight: '#fef3ee',
          coralLight: '#feeff0',
          mauveLight: '#f8ecf1',
          purpleLight: '#f2eff5',
          oceanLight: '#eaf0f5',
          oceanDark: '#28465f',
          purpleDark: '#544660',
        },
      },
      fontFamily: {
        serif: ['Georgia', 'serif'],
        sans: ['Inter', 'system-ui', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
