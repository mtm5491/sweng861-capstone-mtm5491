export async function createMealPlanEntry(entry) {
  const res = await fetch("http://localhost:8080/api/mealPlanEntry", {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(entry)
  });

  if (!res.ok) throw new Error("Failed to create meal plan entry");
  return res.json();
}