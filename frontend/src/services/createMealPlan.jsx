const API_URL = "http://localhost:8080";

export async function createMealPlan(mealPlan) {
  const res = await fetch(`${API_URL}/api/mealPlans`, {
    method: "POST",
    credentials: "include",
    // headers: {
    //   "Content-Type": "application/json",
    //   "X-Test-User": "test-user-123"   // remove later when OAuth is working
    // },
    body: JSON.stringify(mealPlan)
  });

  if (!res.ok) {
    throw new Error("Failed to add mealPlan");
  }

  return res.json();
}
