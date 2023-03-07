/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: "class",
  content: [
    "./src/**/*.{js,jsx,ts,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#00333F',
        'primary-focus': '#00333F',
        'secondary-background': '#EDEFF1',
        'dark-text': '#333333',
        'back-dark': '#f4EDED',
        'back-light': '#f8f5f5',
        'secondary': '#006351',
        'secondary-dark': '#0E4956',
        'error-focus': '#FF4D4F'
      }
    },
  },
  plugins: [],
}
