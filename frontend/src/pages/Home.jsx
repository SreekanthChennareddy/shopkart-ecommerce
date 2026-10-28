import React, { useEffect, useState } from "react";
import { productApi } from "../api/productApi";
import ProductCard from "../components/common/ProductCard";

export default function Home() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await productApi.getProducts(0, 10);
        if (response.success) {
          setProducts(response.data.content || []);
        }
      } catch (err) {
        console.error("Failed to load products", err);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 space-y-6">
      {/* Deals Header Section */}
      <div className="bg-white p-4 rounded-sm shadow-sm flex items-center justify-between border-b border-gray-100">
        <div>
          <h2 className="text-xl font-bold text-gray-800">
            Best of Electronics & Mobiles
          </h2>
          <p className="text-xs text-fk-muted">
            Top deals refreshed every hour
          </p>
        </div>
        <button className="bg-fk-blue text-white text-xs font-semibold px-4 py-2 rounded-sm shadow-sm hover:bg-fk-blue-dark transition">
          VIEW ALL
        </button>
      </div>

      {/* Products Grid */}
      {loading ? (
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
          {[1, 2, 3, 4, 5].map((i) => (
            <div
              key={i}
              className="bg-white h-72 rounded-sm shadow-sm animate-pulse p-4 flex flex-col justify-between"
            >
              <div className="w-full h-40 bg-gray-200 rounded" />
              <div className="h-4 bg-gray-200 rounded w-3/4" />
              <div className="h-4 bg-gray-200 rounded w-1/2" />
            </div>
          ))}
        </div>
      ) : products.length > 0 ? (
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      ) : (
        <div className="bg-white p-12 text-center rounded-sm shadow-sm">
          <p className="text-gray-500">No products found yet.</p>
        </div>
      )}
    </div>
  );
}
