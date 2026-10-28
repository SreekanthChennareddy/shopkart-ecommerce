import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { authApi } from "../api/authApi";
import { setCredentials } from "../store/slices/authSlice";

export default function Register() {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phone: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await authApi.register(formData);
      if (response.success) {
        dispatch(setCredentials(response.data));
        navigate("/");
      }
    } catch (err) {
      setError(
        err.response?.data?.message || "Registration failed. Please try again.",
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
            <h2 className="text-3xl font-bold mb-3">
              Looks like you're new here!
            </h2>
            <p className="text-gray-200 text-sm leading-relaxed">
              Sign up with your details to get started with ShopKart
            </p>
          </div>
          <div className="opacity-90">
            <span className="text-4xl">🚀</span>
          </div>
        </div>

        {/* Right Form Panel */}
        <div className="w-3/5 p-8 flex flex-col justify-between">
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-600 text-sm p-2 rounded-sm">
                {error}
              </div>
            )}

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                  First Name
                </label>
                <input
                  type="text"
                  name="firstName"
                  required
                  value={formData.firstName}
                  onChange={handleChange}
                  placeholder="First Name"
                  className="w-full border-b border-gray-300 py-1.5 text-sm focus:outline-none focus:border-fk-blue"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                  Last Name
                </label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  placeholder="Last Name"
                  className="w-full border-b border-gray-300 py-1.5 text-sm focus:outline-none focus:border-fk-blue"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                Email
              </label>
              <input
                type="email"
                name="email"
                required
                value={formData.email}
                onChange={handleChange}
                placeholder="Enter Email"
                className="w-full border-b border-gray-300 py-1.5 text-sm focus:outline-none focus:border-fk-blue"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                Phone
              </label>
              <input
                type="text"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="10-digit Mobile Number"
                className="w-full border-b border-gray-300 py-1.5 text-sm focus:outline-none focus:border-fk-blue"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                Password
              </label>
              <input
                type="password"
                name="password"
                required
                value={formData.password}
                onChange={handleChange}
                placeholder="Set Password (min 6 chars)"
                className="w-full border-b border-gray-300 py-1.5 text-sm focus:outline-none focus:border-fk-blue"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-fk-orange hover:bg-orange-600 text-white font-semibold py-2.5 rounded-sm shadow-sm transition disabled:opacity-60 mt-4"
            >
              {loading ? "Creating Account..." : "CONTINUE"}
            </button>
          </form>

          <div className="text-center pt-4">
            <Link
              to="/login"
              className="text-sm font-semibold text-fk-blue hover:underline"
            >
              Existing User? Log in
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
