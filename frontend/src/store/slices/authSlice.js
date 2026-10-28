import { createSlice } from "@reduxjs/toolkit";
import { STORAGE_KEYS } from "../../utils/constants";

const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
const userStr = localStorage.getItem(STORAGE_KEYS.USER);

let user = null;
try {
  user = userStr ? JSON.parse(userStr) : null;
} catch (e) {
  console.error("Failed to parse user from localStorage", e);
}

const initialState = {
  user,
  token: token || null,
  refreshToken: refreshToken || null,
  isAuthenticated: !!token,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setCredentials: (state, action) => {
      const {
        token,
        refreshToken,
        userId,
        email,
        firstName,
        role,
        assignedShard,
      } = action.payload;

      const userData = {
        userId,
        email,
        firstName,
        role,
        assignedShard,
      };

      state.user = userData;
      state.token = token;
      state.refreshToken = refreshToken;
      state.isAuthenticated = true;

      // Persist to localStorage
      localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, token);
      if (refreshToken) {
        localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
      }
      localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(userData));
    },

    updateAccessToken: (state, action) => {
      const { token, refreshToken } = action.payload;
      state.token = token;
      localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, token);
      if (refreshToken) {
        state.refreshToken = refreshToken;
        localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
      }
    },

    logout: (state) => {
      state.user = null;
      state.token = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      // Clear localStorage
      localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
      localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
      localStorage.removeItem(STORAGE_KEYS.USER);
    },
  },
});

export const { setCredentials, updateAccessToken, logout } = authSlice.actions;
export default authSlice.reducer;
