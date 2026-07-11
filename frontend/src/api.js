import axios from "axios";

const TOKEN_KEY = "token";

// Läuft die App hinter einem dynamischen Pfad-Präfix (z. B. Home Assistant
// Ingress: /api/hassio_ingress/<token>/), spiegelt der von nginx injizierte
// <base href>-Tag diesen Präfix wider (siehe auch router/index.js). Ein
// absoluter Pfad wie "/api" würde vom Browser immer gegen die Domain-Wurzel
// aufgelöst, unabhängig vom <base>-Tag – daher hier den vollständigen,
// bereits präfixierten Pfad selbst zusammensetzen statt einer fest
// einkompilierten oder rein relativen Zeichenkette zu vertrauen.
const baseHref = document.querySelector("base")?.getAttribute("href") || "/";
export const appBasePath = new URL(baseHref, window.location.origin).pathname.replace(/\/$/, "");

const api = axios.create({
  baseURL: `${appBasePath}/api`,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      localStorage.removeItem(TOKEN_KEY);
      const loginPath = `${appBasePath}/login`;
      if (window.location.pathname !== loginPath) {
        window.location.assign(loginPath);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
