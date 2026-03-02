// For REACT hooks 
// -- > useState = store and show error messages
// -- > useEffect = read URL parameters when the page loads
import { useState, useEffect } from "react";

export default function Login() {
  // creats an error state, starting as null
  // USE setError("message") to display error
  const [error, setError] = useState(null);

  // runs when page loads
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const err = params.get("error");

    if (err === "unauthorized") {
      setError("Please log in to continue.");
    } else if (err === "failed") {
      setError("Login failed. Please try again.");
    }
  }, []);

  // runs when user clicks the login button
  function handleGoogleLogin() {
    // Redirect to backend OAuth start [runs when you run the backend]
    // window.location.href = "${API_URL}/auth/login";
    // const API_URL = import.meta.env.VITE_API_URL;
    window.location.href = `http://localhost:8080/auth/login`;

  }

  return (
    <div style={{ padding: "20px" }}>
      <h1>Login</h1>

      {/* when error is null, show nothing
          when error has text, show red  */}
      {error && (
        <p style={{ color: "red", marginBottom: "10px" }}>
          {error}
        </p>
      )}

      <button
      // On Click --> run handleGoogleLogin
        onClick={handleGoogleLogin}
        style={{
          padding: "10px 20px",
          background: "#4285F4",
          color: "white",
          border: "none",
          borderRadius: "4px",
          cursor: "pointer",
          fontSize: "1rem",
        }}
      >
        Login with Google
      </button>
    </div>
  );
}