import React from "react";
import { Star, Heart } from "lucide-react";

export default function ProductCard({ product }) {
  // Grab the primary variant and primary image
  const variant = product.variants?.[0];
  const primaryImage =
    variant?.images?.find((img) => img.isPrimary)?.imageUrl ||
    variant?.images?.[0]?.imageUrl ||
    "https://via.placeholder.com/300x300?text=No+Image";

  // Calculate discount percentage
  const mrp = variant?.price || 0;
  const sellingPrice = variant?.sellingPrice || mrp;
  const discountPercent =
    mrp > sellingPrice ? Math.round(((mrp - sellingPrice) / mrp) * 100) : 0;

  return (
    <div className="bg-white p-4 rounded-sm shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between group cursor-pointer border border-transparent hover:border-gray-200">
      {/* Top Image Section */}
      <div className="relative w-full h-52 flex items-center justify-center overflow-hidden mb-3">
        <button
          className="absolute top-1 right-1 text-gray-300 hover:text-red-500 transition-colors z-10"
          title="Add to Wishlist"
        >
          <Heart size={20} />
        </button>
        <img
          src={primaryImage}
          alt={product.title}
          className="max-h-full max-w-full object-contain group-hover:scale-105 transition-transform duration-300"
          onError={(e) => {
            e.target.src = "https://via.placeholder.com/300x300?text=ShopKart";
          }}
        />
      </div>

      {/* Product Details Section */}
      <div className="flex flex-col gap-1">
        <span className="text-xs font-semibold text-fk-muted uppercase tracking-wider">
          {product.brand}
        </span>
        <h3
          className="text-sm font-medium text-gray-900 line-clamp-2 group-hover:text-fk-blue transition-colors"
          title={product.title}
        >
          {product.title}
        </h3>

        {/* Rating Badge */}
        <div className="flex items-center gap-1.5 my-1">
          <span className="bg-fk-green text-white text-xs font-bold px-1.5 py-0.5 rounded flex items-center gap-0.5">
            4.6 <Star size={10} fill="currentColor" />
          </span>
          <span className="text-xs text-fk-muted">(1,248)</span>
        </div>

        {/* Price Row */}
        <div className="flex items-baseline gap-2 mt-1">
          <span className="text-base font-bold text-gray-900">
            ₹{sellingPrice.toLocaleString("en-IN")}
          </span>
          {discountPercent > 0 && (
            <>
              <span className="text-xs text-fk-muted line-through">
                ₹{mrp.toLocaleString("en-IN")}
              </span>
              <span className="text-xs font-bold text-fk-green">
                {discountPercent}% off
              </span>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
