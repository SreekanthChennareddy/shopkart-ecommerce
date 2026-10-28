import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(), // <-- Tailwind v4 plugin
  ],
  server: {
    port: 3000, // Matches the CORS allowed origin in our API Gateway
  },
});
