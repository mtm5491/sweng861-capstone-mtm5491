/**
 * Recipe details component
 * Displays information for a single recipe item
 * fetches recipes, handles loading, error, and success, etc.
 */
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function RecipeDetails() {
  // extract /recipes/id{} from URL
  const { id } = useParams();

  // COMPONENT STATES
  const [recipe, setRecipe] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const API_URL = import.meta.env.VITE_API_URL;

  /**
   * Fetches recipe data when component mounts or ID changes
   */
  useEffect(() => {
  async function loadRecipe() {
    try {
      const res = await fetch(`http://localhost:8080/api/recipes/${id}`, {
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
      setRecipe(data.map);
    } catch (err) {
      setError("Network error while loading item.");
    } finally {
      setLoading(false);
    }

    }

    loadRecipe();
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

  // On success, show recipe
  return (
    <div style={{ marginLeft: "220px", padding: "20px" }}>
      
      <h2>Recipe Details</h2>

      <p><strong>Name:</strong> {recipe.name}</p>
      <p><strong>Description:</strong> {recipe.description}</p>
      <p><strong>Instructions:</strong> {recipe.instructions}</p>
      <p><strong>Ingredients:</strong></p>
      <ul>
        {(recipe.ingredients?.myArrayList || []).map((ing, i) => (
          <li key={i}>
            {ing.map.quantity} {ing.map.unit} {ing.map.name}
          </li>
        ))}
      </ul>


      <pre
        // style={{
        //   background: "#f4f4f4",
        //   padding: "10px",
        //   borderRadius: "4px",
        //   maxWidth: "400px"
        // }}
      >
      </pre>

{/* TODO: When I integrate edit, this will be the button and re-route */}
      {/* <button
        onClick={() => (window.location.href = `/recipes/${id}/edit`)}
        style={{
          marginTop: "20px",
          padding: "8px 14px",
          background: "#4CAF50",
          color: "white",
          border: "none",
          borderRadius: "4px",
          cursor: "pointer"
        }}
      >
        Edit
      </button> */}
    </div>
  );
}
