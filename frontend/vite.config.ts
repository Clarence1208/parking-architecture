import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

const proxyTarget = process.env.VITE_PROXY_TARGET ?? 'http://localhost:8080'

export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    port: 80,
    strictPort: true,
    proxy: {
      '/api': {
        target: proxyTarget,
        changeOrigin: true,
      },
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
  },
} )
