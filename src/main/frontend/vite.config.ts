import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/mandi',
  build: {
    outDir: '../resources/static/',
    emptyOutDir: true,
  },
})
