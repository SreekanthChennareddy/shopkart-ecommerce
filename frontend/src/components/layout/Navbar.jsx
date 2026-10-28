import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import { Search, ShoppingCart, User, LogOut } from "lucide-react";
import { logout } from "../../store/slices/authSlice";

export default function Navbar() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { user, isAuthenticated } = useSelector((state) => state.auth);
  //const cartCount = useSelector((state) => state.cart.totalQuantity);

  const cartCount = 0;

  const handleLogout = () => {
    dispatch(logout());
    navigate("/login");
  };

  return (
    <header className="bg-fk-blue sticky top-0 z-50 shadow-md">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between gap-6">
        {/* Brand Logo */}
        <Link to="/" className="flex flex-col items-start leading-none group">
          <span className="text-white italic font-bold text-2xl tracking-wide">
            ShopKart
          </span>
          <span className="text-xs text-fk-yellow italic flex items-center gap-1 group-hover:underline">
            Explore <span className="text-white font-semibold">Plus</span>
            <span className="text-fk-yellow font-bold text-sm">✦</span>
          </span>
        </Link>

        {/* Global Search Bar */}
        <div className="flex-1 max-w-2xl relative">
          <input
            type="text"
            placeholder="Search for Products, Brands and More"
            className="w-full bg-white text-gray-800 placeholder-gray-500 text-sm px-4 py-2.5 rounded-sm shadow-inner focus:outline-none focus:ring-1 focus:ring-fk-blue"
          />
          <button className="absolute right-3 top-1/2 -translate-y-1/2 text-fk-blue hover:opacity-80">
            <Search size={18} />
          </button>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-6 text-white font-medium text-sm">
          {isAuthenticated && user ? (
            <div className="flex items-center gap-4">
              <span className="flex items-center gap-1.5 text-white bg-fk-blue-dark px-3 py-1.5 rounded-sm">
                <User size={16} />
                Hi, {user.firstName || user.email.split("@")[0]}
              </span>
              <button
                onClick={handleLogout}
                title="Logout"
                className="flex items-center gap-1 hover:text-red-200 transition-colors"
              >
                <LogOut size={16} />
                <span>Logout</span>
              </button>
            </div>
          ) : (
            <Link
              to="/login"
              className="bg-white text-fk-blue px-8 py-1.5 rounded-sm font-semibold hover:bg-gray-50 transition shadow-sm"
            >
              Login
            </Link>
          )}

          {/* Cart Icon with Redux badge */}
          <Link
            to="/cart"
            className="flex items-center gap-2 hover:text-fk-yellow transition-colors"
          >
            <div className="relative">
              <ShoppingCart size={20} />
              {cartCount > 0 && (
                <span className="absolute -top-2 -right-2 bg-fk-yellow text-gray-900 font-bold text-xs rounded-full h-4 w-4 flex items-center justify-center">
                  {cartCount}
                </span>
              )}
            </div>
            <span className="font-semibold">Cart</span>
          </Link>
        </div>
      </div>
    </header>
  );
}
