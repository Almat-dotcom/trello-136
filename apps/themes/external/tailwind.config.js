/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: "class",
  content: [
    "./src/**/*.{js,jsx,ts,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#B0222E',
        'primary-focus': '#940101',
        'back-dark': '#f4EDED',
        'back-light': '#f8f5f5',
        'secondary': '#006351',
        'secondary-dark': '#0E4956'
      }
    },
  },
  plugins: [],
}
