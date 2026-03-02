import { useEffect, useState } from "react";
import { useNavigate, Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.jsx";

/**
 * Lists all recipe data for the user
 * 
 * fetches recipe from backend, handles loading and errors, shows success, provides detail link
 * that reroutes to details page
 */
export default function Recipe() {
  const auth = useAuth();
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const params = new URLSearchParams(window.location.search);
  const saved = params.get("saved");

  const API_URL = import.meta.env.VITE_API_URL;

   /**
   * Fetch recipe for logged-in user
   */
  async function fetchRecipes() {
    try {
      // get recipe from api
      const res = await fetch(`http://localhost:8080/api/recipes`, {
        credentials: "include" // cookie included
      });

      if (!res.ok) {
        throw new Error("Failed to load recipes");
      }
      const data = await res.json();
      setRecipes(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  /**
   * Once authenticated, load recipes.
   */
  useEffect(() => {
    if (auth === true) {
      fetchRecipes();
    }
  }, [auth]);

  // Handle Auth States
  if (auth === null) {
    return (
      <div style={{ padding: "20px", marginLeft: "220px" }}>
        <h1>My Recipes</h1>
        <p>Checking login…</p>
      </div>
    );
  }
  if (auth === false) {
    return <Navigate to="/login" replace />;
  }

  // Loading and Error states
  if (loading) {
    return (
      <div style={{ padding: "20px", marginLeft: "220px" }}>
        <h1>My Recipes</h1>
        <p>Loading...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: "20px", marginLeft: "220px" }}>
        <h1>My Recipes</h1>
        <p style={{ color: "red" }}>{error}</p>
      </div>
    );
  }


  return (
    <div style={{ padding: "20px", marginLeft: "220px" }}>
      <h1>My Recipes</h1>

      <button
        onClick={() => navigate("/add-recipe")}
        style={{
          marginBottom: "20px",
          padding: "8px 16px",
          background: "#4CAF50",
          color: "white",
          border: "none",
          borderRadius: "4px",
          cursor: "pointer",
        }}
      >
        Add Recipe
      </button>

      {/* empty list */}
      {recipes.length === 0 ? (
        <p>No recipes found.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {recipes.map((f) => (
            <li
              key={f.id}
              style={{
                padding: "12px",
                marginBottom: "10px",
                border: "1px solid #444",
                borderRadius: "6px",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <div>
                <strong>{f.name}</strong>
                {f.description && <span> (Description: {f.description})</span>}
              </div>

              <button
                onClick={() => navigate(`/recipes/${f.id}`)}
                style={{
                  padding: "6px 12px",
                  background: "#2196F3",
                  color: "white",
                  border: "none",
                  borderRadius: "4px",
                  cursor: "pointer",
                }}
              >
                View Details
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
