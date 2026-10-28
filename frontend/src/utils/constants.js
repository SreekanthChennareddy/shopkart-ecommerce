export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
export const APP_NAME = import.meta.env.VITE_APP_NAME || "ShopKart";

export const STORAGE_KEYS = {
  ACCESS_TOKEN: "shopkart_access_token",
  REFRESH_TOKEN: "shopkart_refresh_token",
  USER: "shopkart_user",
  CART: "shopkart_cart",
};
