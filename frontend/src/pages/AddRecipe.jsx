/**
 * Add recipe component
 * User can manually enter reci[e] items into their account, which gets saved
 * 
 * JSON in the backend
 */

import { useState } from "react";
import { createRecipe } from "../services/createRecipe";

export default function AddRecipe() {
  // FORM FIELDS
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [instructions, setInstructions] = useState("");
  const [ingredients, setIngredients] = useState([{name: "", quantity: "", unit: ""}]);

  // UI STATE
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);


  function handleIngredientChange(index, field, value) {
    const updated = [...ingredients];
    updated[index][field] = value;
    setIngredients(updated);
  }

  function addIngredientRow() {
    setIngredients([...ingredients, { name: "", quantity: "", unit: "" }]);
  }

  function removeIngredientRow(index) {
    const updated = ingredients.filter((_, i) => i !== index);
    setIngredients(updated);
  }

  /**
   * Handles the form submission and sends POST request to backend
   * to create a new recipe item   */
  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError(null);

    const recipe = {
      name,
      description,
      instructions,
      ingredients
    };
    try {
        const result = await createRecipe(recipe);
        console.log("Created recipe:", result);

        setSuccess(true);

        // Reset form
        setName("");
        setDescription("");
        setInstructions("");
        setIngredients([{ name: "", quantity: "", unit: "" }]);

      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }

    }

 return (
    <div style={{ marginLeft: "220px", padding: "20px" }}>
      <h2>Add Recipe</h2>
      {/* Success Message */}
      {success && (
        <div style={{ 
          padding: "10px", 
          background: "#d4edda", 
          color: "#155724", 
          borderRadius: "4px",
          marginBottom: "15px"
        }}>
          Recipe was successfully saved!
          <div style={{ marginTop: "10px" }}>
            <button
              onClick={() => (window.location.href = "/recipes")}
              style={{
                padding: "6px 12px",
                background: "#4CAF50",
                color: "white",
                border: "none",
                borderRadius: "4px",
                cursor: "pointer"
              }}
            >
              Go to My Recipes
            </button>
            <div style={{ height: "5px" }}></div>
            <button
              onClick={() => (window.location.href = "/add-recipe")}
              style={{
                padding: "6px 12px",
                background: "#4CAF50",
                color: "white",
                border: "none",
                borderRadius: "4px",
                cursor: "pointer"
              }}
            >
              Add Another Recipe
            </button>
          </div>
        </div>
      )}

      {/* Error Message */}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {/* Loading Indicator */}
      {loading && <p>Adding recipe...</p>}

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
            placeholder="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          />

          <input
            type="text"
            placeholder="Instructions"
            value={instructions}
            onChange={(e) => setInstructions(e.target.value)}
            required
          />
          <h3>Ingredients</h3>

        {ingredients.map((ing, index) => (
          <div key={index} style={{ display: "flex", gap: "10px", marginBottom: "10px" }}>
            <input
              type="text"
              placeholder="Name"
              value={ing.name}
              onChange={e => handleIngredientChange(index, "name", e.target.value)}
            />
            <input
              type="text"
              placeholder="Qty"
              value={ing.quantity}
              onChange={e => handleIngredientChange(index, "quantity", e.target.value)}
            />
            <input
              type="text"
              placeholder="Unit"
              value={ing.unit}
              onChange={e => handleIngredientChange(index, "unit", e.target.value)}
            />

            <button type="button" onClick={() => removeIngredientRow(index)}>
              Remove
            </button>
          </div>
        ))}


          <button type="button" onClick={addIngredientRow}>
            + Add Ingredient
          </button>


          <button type="submit" disabled={loading}>
            Add Recipe
          </button>
        </form>
      )}
    </div>
  );
}
