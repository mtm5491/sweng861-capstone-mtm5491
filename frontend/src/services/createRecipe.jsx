const API_URL = "http://localhost:8080";

export async function createRecipe(recipe) {
  const res = await fetch(`${API_URL}/api/recipes`, {
    method: "POST",
    credentials: "include",
    // headers: {
    //   "Content-Type": "application/json",
    //   "X-Test-User": "test-user-123"   // remove later when OAuth is working
    // },
    body: JSON.stringify(recipe)
  });

  if (!res.ok) {
    throw new Error("Failed to add recipe");
  }

  return res.json();
}
