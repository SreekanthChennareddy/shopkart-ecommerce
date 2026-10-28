import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { authApi } from "../api/authApi";
import { setCredentials } from "../store/slices/authSlice";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await authApi.login({ email, password });
      if (response.success) {
        // Dispatch to Redux!
        dispatch(setCredentials(response.data));
        navigate("/");
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Login failed. Please check your credentials.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-64px)] flex items-center justify-center py-10 px-4">
      <div className="bg-white w-full max-w-3xl flex shadow-lg rounded-sm overflow-hidden min-h-[500px]">
        {/* Left Brand Panel */}
        <div className="bg-fk-blue w-2/5 p-8 flex flex-col justify-between text-white">
          <div>
            <h2 className="text-3xl font-bold mb-3">Login</h2>
            <p className="text-gray-200 text-sm leading-relaxed">
              Get access to your Orders, Wishlist and Recommendations
            </p>
          </div>
          <div className="opacity-90">
            <span className="text-4xl">🛍️</span>
          </div>
        </div>

        {/* Right Form Panel */}
        <div className="w-3/5 p-10 flex flex-col justify-between">
          <form onSubmit={handleSubmit} className="space-y-6">
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-600 text-sm p-3 rounded-sm">
                {error}
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                Email Address
              </label>
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter Email"
                className="w-full border-b border-gray-300 py-2 text-sm focus:outline-none focus:border-fk-blue transition-colors"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                Password
              </label>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter Password"
                className="w-full border-b border-gray-300 py-2 text-sm focus:outline-none focus:border-fk-blue transition-colors"
              />
            </div>

            <p className="text-xs text-gray-500">
              By continuing, you agree to ShopKart's{" "}
              <span className="text-fk-blue cursor-pointer">Terms of Use</span>{" "}
              and{" "}
              <span className="text-fk-blue cursor-pointer">
                Privacy Policy
              </span>
              .
            </p>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-fk-orange hover:bg-orange-600 text-white font-semibold py-3 rounded-sm shadow-sm transition disabled:opacity-60"
            >
              {loading ? "Logging in..." : "Login"}
            </button>
          </form>

          <div className="text-center pt-6">
            <Link
              to="/register"
              className="text-sm font-semibold text-fk-blue hover:underline"
            >
              New to ShopKart? Create an account
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
