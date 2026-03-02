import { useEffect, useState } from "react";

export function RecipeHover({ recipeId }) {
  const [recipe, setRecipe] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/recipes/${recipeId}`, { credentials: "include" })
      .then(res => res.json())
      .then(data => setRecipe(data.map)); // because your backend wraps in map
  }, [recipeId]);

  if (!recipe) return <div>Loading...</div>;

  return (
    <div>
      <strong>{recipe.name}</strong><br />
      {recipe.description}
    </div>
  );
}
