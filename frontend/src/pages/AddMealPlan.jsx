/**
 * Add meal plan
 * User can create a meal plan.
 * 
 * JSON in the backend
 */

import { useState } from "react";
import { createMealPlan } from "../services/createMealPlan";

export default function AddMealPlan() {
  // FORM FIELDS
  const [name, setName] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  
  // UI STATE
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  /**
   * Handles the form submission and sends POST request to backend
   * to create a new Meal Plan item   */
  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError(null);

    const mealPlan = {
      name,
      startDate,
      endDate
    };
    try {
        const result = await createMealPlan(mealPlan);
        console.log("Created mealPlan:", result);

        setSuccess(true);

        // Reset form
        setName("");
        setStartDate("");
        setEndDate("");

      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }

    }

 return (
    <div style={{ marginLeft: "220px", padding: "20px" }}>
      <h2>Add Meal Plan</h2>
      {/* Success Message */}
      {success && (
        <div style={{ 
          padding: "10px", 
          background: "#d4edda", 
          color: "#155724", 
          borderRadius: "4px",
          marginBottom: "15px"
        }}>
          Meal Plan was successfully saved!
          <div style={{ marginTop: "10px" }}>
            <button
              onClick={() => (window.location.href = "/mealPlans")}
              style={{
                padding: "6px 12px",
                background: "#4CAF50",
                color: "white",
                border: "none",
                borderRadius: "4px",
                cursor: "pointer"
              }}
            >
              Go to My Meal Plans
            </button>
            <div style={{ height: "5px" }}></div>
            <button
              onClick={() => (window.location.href = "/add-mealPlan")}
              style={{
                padding: "6px 12px",
                background: "#4CAF50",
                color: "white",
                border: "none",
                borderRadius: "4px",
                cursor: "pointer"
              }}
            >
              Create Another Meal Plan
            </button>
          </div>
        </div>
      )}

      {/* Error Message */}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {/* Loading Indicator */}
      {loading && <p>Adding meal plan...</p>}

      {/* Form */}
      {!success && (
        <form
          onSubmit={handleSubmit}
          style={{
            display: "flex",
            flexDirection: "column",
            gap: "12px",
            maxWidth: "300px"
          }}
        >
          <input
            type="text"
            placeholder="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Start Date (YYYY-MM-DD)"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            required
          />

          <input
            type="text"
            placeholder="End Date (YYYY-MM-DD)"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            required
          />

          <button type="submit" disabled={loading}>
            Add MealPlan
          </button>
        </form>
      )}
    </div>
  );
}
