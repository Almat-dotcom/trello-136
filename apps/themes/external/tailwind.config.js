/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#7f0000',
        'primary-focus': '#940101',
        'back-dark': '#f4EDED',
        'back-light': '#f8f5f5'
      }
    },
  },
  plugins: [],
}
