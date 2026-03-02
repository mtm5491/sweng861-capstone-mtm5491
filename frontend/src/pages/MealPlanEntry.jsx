import { useState, useEffect } from "react";
import { createMealPlanEntry, getMealPlans } from "../services/mealPlanService";

export default function AddMealPlanEntry({ recipeId }) {
  const [mealPlans, setMealPlans] = useState([]);
  const [mealPlanId, setMealPlanId] = useState("");
  const [date, setDate] = useState("");
  const [mealType, setMealType] = useState("BREAKFAST");

  useEffect(() => {
    async function load() {
      const plans = await getMealPlans();
      setMealPlans(plans);
    }
    load();
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
    alert("Added to meal plan!");
  }

  return (
    <form onSubmit={handleSubmit}>
      <label>Meal Plan:</label>
      <select value={mealPlanId} onChange={e => setMealPlanId(e.target.value)}>
        <option value="">Select a plan</option>
        {mealPlans.map(mp => (
          <option key={mp.id} value={mp.id}>{mp.name}</option>
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
