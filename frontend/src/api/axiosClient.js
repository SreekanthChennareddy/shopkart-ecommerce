import axios from "axios";
import { API_BASE_URL, STORAGE_KEYS } from "../utils/constants";
import { store } from "../store/store";
import { updateAccessToken, logout } from "../store/slices/authSlice";

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request Interceptor: Attach Bearer Token from Redux or Storage
axiosClient.interceptors.request.use(
  (config) => {
    const token =
      store.getState().auth.token ||
      localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// Response Interceptor: Silent Token Refresh
let isRefreshing = false;
let failedQueue = [];
const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    // Handle 401 Unauthorized (token expired)
    if (error.response?.status === 401 && !originalRequest._retry) {
      // Avoid looping on login/refresh endpoints
      if (
        originalRequest.url.includes("/api/v1/auth/login") ||
        originalRequest.url.includes("/api/v1/auth/refresh")
      ) {
        return Promise.reject(error);
      }
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return axiosClient(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }
      originalRequest._retry = true;
      isRefreshing = true;
      const state = store.getState();
      const refreshToken =
        state.auth.refreshToken ||
        localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
      const email = state.auth.user?.email;
      if (!refreshToken || !email) {
        store.dispatch(logout());
        window.location.href = "/login";
        return Promise.reject(error);
      }
      try {
        const response = await axios.post(
          `${API_BASE_URL}/api/v1/auth/refresh`,
          {
            refreshToken,
            email,
          },
        );
        const { accessToken, refreshToken: newRefreshToken } =
          response.data.data;
        // Dispatch token update to Redux
        store.dispatch(
          updateAccessToken({
            token: accessToken,
            refreshToken: newRefreshToken,
          }),
        );
        axiosClient.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        processQueue(null, accessToken);
        return axiosClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        store.dispatch(logout());
        window.location.href = "/login";
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  },
);
export default axiosClient;
