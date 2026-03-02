import { useState, useEffect } from "react";
// import { getRecipes } from "../services/recipeService";
import { createMealPlanEntry } from "../services/createMealPlanEntry";

export default function AddRecipeToMealPlan({ mealPlanId, onAdded }) {
  const [recipes, setRecipes] = useState([]);
  const [recipeId, setRecipeId] = useState("");
  const [date, setDate] = useState("");
  const [mealType, setMealType] = useState("BREAKFAST");
  const [error, setError] = useState(null);


    useEffect(() => {
    async function loadRecipes() {
      try {
        const res = await fetch("http://localhost:8080/api/recipes", {
          credentials: "include"
        });

        console.log("Recipes status:", res.status);

        const data = await res.json();
      console.log("Recipes data:", data);

      
      setRecipes(data);

    } catch (err) {
      setError("Network error while loading recipes.");
    }

    }


    loadRecipes();
  }, []);


  async function handleSubmit(e) {
    e.preventDefault();

    const entry = {
      mealPlanId: Number(mealPlanId),
      recipeId: Number(recipeId),
      date,
      mealType
    };

    await createMealPlanEntry(entry);
    onAdded(); // refresh parent page

    setRecipeId("");
    setDate("");
    setMealType("BREAKFAST");

  }

  return (
    <form onSubmit={handleSubmit}>
      
      <label>Recipe:</label>
      <select value={recipeId} onChange={e => setRecipeId(e.target.value)}>
        <option value="">Select a recipe</option>
        {recipes.map(r => (
          <option key={r.id} value={r.id}>{r.name}</option>
        ))}
      </select>

      <label>Date:</label>
      <input type="date" value={date} onChange={e => setDate(e.target.value)} />

      <label>Meal Type:</label>
      <select value={mealType} onChange={e => setMealType(e.target.value)}>
        <option>BREAKFAST</option>
        <option>LUNCH</option>
        <option>DINNER</option>
        <option>SNACK</option>
      </select>

      <button type="submit">Add</button>
    </form>
  );
}