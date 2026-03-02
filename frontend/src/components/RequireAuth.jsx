/**
 * Wrapper used to protect the frontend routes
 * Checks if user is authenticated, shows loading while checking, redirects
 * unauthenticated users.
 */


import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";

export default function RequireAuth({ children }) {
  // null = loading, false = not auth, true = auth
  const [auth, setAuth] = useState(null);
  useEffect(() => {
    async function checkAuth() {
      try {
        // Call a protected backend endpoint
        const API_URL = import.meta.env.VITE_API_URL;
        const res = await fetch(`${API_URL}/api/foods`, {
          credentials: "include",
        });
        // If backend returns 200, the session is valid
        if (res.status === 200) {
          setAuth(true);
        } else {
          setAuth(false);
        }
      } catch (err) {
        // Network or server error -> treat as not authenticated
        setAuth(false);
      }
    }

    checkAuth();
  }, []);

  // Still checking authentication
  if (auth === null) {
    return <div>Loading...</div>;
  }
  // Not authenticated, redirect to login
  if (auth === false) {
    return <Navigate to="/login" replace />;
  }

  // Authenticated, render protected content
  return children;
}
