import axiosClient from "./axiosClient";

export const authApi = {
  login: async (credentials) => {
    const response = await axiosClient.post("/api/v1/auth/login", credentials);
    return response.data;
  },

  register: async (userData) => {
    const response = await axiosClient.post("/api/v1/auth/register", userData);
    return response.data;
  },

  refresh: async (refreshData) => {
    const response = await axiosClient.post(
      "/api/v1/auth/refresh",
      refreshData,
    );
    return response.data;
  },
};
