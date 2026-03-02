/**
 * authentication-checking hook used by protected routes
 * Verifies user's session cookie.
 */
import { useEffect, useState } from "react";

export function useAuth() {
  // null = loading, false = not authenticated, true = authenticated
  const [auth, setAuth] = useState(null);

  useEffect(() => {
    async function check() {
      try {
        // Call a protected backend endpoint to validate the session cookie
        const API_URL = import.meta.env.VITE_API_URL;
        const res = await fetch(`${API_URL}/api/foods`, {
          // ensures sessionId cookie is sent
          credentials: "include",
        });

        console.log("Auth check response:", res.status);
        
        // If backend returns 200, user is authenticated
        setAuth(res.status === 200);
      } catch {
        // Network or server error → treat as not authenticated
        setAuth(false);
      }
    }

    check();
  }, []);

  return auth;
}
