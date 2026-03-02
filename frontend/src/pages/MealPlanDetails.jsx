/**
 * Recipe details component
 * Displays information for a single recipe item
 * fetches recipes, handles loading, error, and success, etc.
 */
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { RecipeHover } from "../components/RecipeHover";
import AddRecipeToMealPlan from "../components/AddRecipeToMealPlan";
// import { getMealPlanById } from "../services/mealPlanService";


export default function MealPlanDetails() {
  // extract /mealPlans/id{} from URL
  const { id } = useParams();

  // COMPONENT STATES
  const [mealPlan, setMealPlan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  /**
   * Fetches mealPlan data when component mounts or ID changes
   */
  
    
  async function loadMealPlan() {
    try {
      const res = await fetch(`http://localhost:8080/api/mealPlans/${id}`, {
        credentials: "include"
      });

      console.log("Response status:", res.status);

      if (res.status === 404) {
        setError("This item does not exist or has been deleted.");
        return;
      }

      if (res.status === 403) {
        setError("You are not authorized to view this item.");
        return;
      }
      const data = await res.json();
      console.log("Data:", data);
      setMealPlan(data);
    } catch (err) {
      setError("Network error while loading item.");
    } finally {
      setLoading(false);
    }

    }
    useEffect(() => {
      loadMealPlan();
    }, [id]);

  

  /** UI States */
  if (loading) {
    return (
      <div style={{ marginLeft: "220px", padding: "20px" }}>
        <p>Loading...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ marginLeft: "220px", padding: "20px" }}>
        <p style={{ color: "red" }}>{error}</p>
      </div>
    );
  }

  // On success, show meal plans
  return (
    <div style={{ marginLeft: "220px", padding: "20px" }}>
      <h2>Meal Plan Details</h2>

      <p><strong>Name:</strong> {mealPlan.map.name}</p>
      <p><strong>Start Date:</strong> {mealPlan.map.startDate}</p>
      <p><strong>End Date:</strong> {mealPlan.map.endDate}</p>
      <h3>Add Recipe to Meal Plan</h3>
      <AddRecipeToMealPlan mealPlanId={id} onAdded={loadMealPlan} />

      <h3>Entries</h3>
      {mealPlan.map.entries?.myArrayList?.length > 0 ? (
        <ul>
          {mealPlan.map.entries.myArrayList.map((entry, i) => (
            <li key={i}>
              {entry.map.date} — {entry.map.mealType} — 
              <span className="tooltip">
                Recipe #{entry.map.recipeId}
                <span className="tooltip-content">
                  <RecipeHover recipeId={entry.map.recipeId} />
                </span>
              </span>
            </li>
          ))}
          </ul>
      ) : (
        <p>No entries yet.</p>
      )}
    </div>
  );
}

