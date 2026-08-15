import axios from 'axios';
import { clearAuth } from '../utils/tokenStorage';

export const AUTH_UNAUTHORIZED_EVENT = 'auth:unauthorized';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('ems_token');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const requestUrl = error.config?.url ?? '';
    const isLoginRequest = requestUrl.includes('/auth/login');

    if (status === 401 && !isLoginRequest) {
      clearAuth();
      window.dispatchEvent(new CustomEvent(AUTH_UNAUTHORIZED_EVENT));
    }

    return Promise.reject(error);
  },
);

export default api;
