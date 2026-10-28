import React, { useEffect, useState } from "react";
import { productApi } from "../../api/productApi";

export default function CategoryBar() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await productApi.getCategories();
        if (response.success) {
          setCategories(response.data);
        }
      } catch (err) {
        console.error("Failed to load categories", err);
      } finally {
        setLoading(false);
      }
    };

    fetchCategories();
  }, []);

  if (loading) {
    return (
      <div className="bg-white shadow-sm border-b border-fk-border py-4">
        <div className="max-w-7xl mx-auto px-4 flex justify-between animate-pulse">
          {[1, 2, 3, 4, 5].map((i) => (
            <div key={i} className="flex flex-col items-center gap-2">
              <div className="w-16 h-16 bg-gray-200 rounded-full" />
              <div className="w-20 h-3 bg-gray-200 rounded" />
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <nav className="bg-white shadow-sm border-b border-fk-border">
      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between overflow-x-auto gap-8 scrollbar-none">
        {categories.map((cat) => (
          <div
            key={cat.id}
            className="flex flex-col items-center gap-1 cursor-pointer group flex-shrink-0"
          >
            <div className="w-16 h-16 flex items-center justify-center overflow-hidden transition-transform group-hover:scale-105">
              <img
                src={cat.iconUrl}
                alt={cat.name}
                className="w-full h-full object-contain"
                onError={(e) => {
                  e.target.src = "https://via.placeholder.com/64?text=Category";
                }}
              />
            </div>
            <span className="text-xs font-semibold text-gray-700 group-hover:text-fk-blue transition-colors text-center">
              {cat.name}
            </span>
          </div>
        ))}
      </div>
    </nav>
  );
}
