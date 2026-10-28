import axiosClient from "./axiosClient";

export const productApi = {
  // 1. Fetch all root categories (Mobiles, Electronics, etc.)
  getCategories: async () => {
    const res = await axiosClient.get("/api/v1/categories");
    return res.data;
  },

  // 2. Fetch paginated products
  getProducts: async (
    page = 0,
    size = 10,
    sortBy = "createdAt",
    sortDir = "desc",
  ) => {
    const res = await axiosClient.get(
      `/api/v1/products?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`,
    );
    return res.data;
  },

  // 3. Fetch single product by ID
  getProductById: async (id) => {
    const res = await axiosClient.get(`/api/v1/products/${id}`);
    return res.data;
  },

  // 4. Request Pre-Signed URL for image uploads
  getPresignedUrl: async (fileData) => {
    const res = await axiosClient.post("/api/v1/media/presigned-url", fileData);
    return res.data;
  },

  // 5. Create a new product (Seller endpoint)
  createProduct: async (productData) => {
    const res = await axiosClient.post("/api/v1/products", productData);
    return res.data;
  },
};
