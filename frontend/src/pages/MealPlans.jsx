import { useEffect, useState } from "react";
import { useNavigate, Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.jsx";

/**
 * Lists all Meal Plan data for the user
 * 
 * fetches Meal Plan from backend, handles loading and errors, shows success, provides detail link
 * that reroutes to details page
 */
export default function MealPlan() {
  const auth = useAuth();
  const [mealPlans, setMealPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const params = new URLSearchParams(window.location.search);
  const saved = params.get("saved");


   /**
   * Fetch mealPlans for logged-in user
   */
  async function fetchMealPlans() {
    try {
      // get mealPlans from api
      const res = await fetch(`http://localhost:8080/api/mealPlans`, {
        credentials: "include" // cookie included
      });

      if (!res.ok) {
        throw new Error("Failed to load meal plans");
      }
      const data = await res.json();
      setMealPlans(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  /**
   * Once authenticated, load meal plans.
   */
  useEffect(() => {
    if (auth === true) {
      fetchMealPlans();
    }
  }, [auth]);

  // Handle Auth States
  if (auth === null) {
    return (
      <div style={{ padding: "20px", marginLeft: "220px" }}>
        <h1>My Meal Plans</h1>
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
        <h1>My Meal Plans</h1>
        <p>Loading...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: "20px", marginLeft: "220px" }}>
        <h1>My Meal Plans</h1>
        <p style={{ color: "red" }}>{error}</p>
      </div>
    );
  }


  return (
    <div style={{ padding: "20px", marginLeft: "220px" }}>
      <h1>My Meal Plans</h1>

      <button
        onClick={() => navigate("/add-mealPlan")}
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
        Add Meal Plan
      </button>

      {/* empty list */}
      {mealPlans.length === 0 ? (
        <p>No Meal Plans found.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0 }}>
          {mealPlans.map((f) => (
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
                {f.startDate && <span> (Start: {f.startDate})</span>}
                {f.endDate && <span> (End: {f.endDate})</span>}
              </div>

              <button
                onClick={() => navigate(`/mealPlans/${f.id}`)}
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
