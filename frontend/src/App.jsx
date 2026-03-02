import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import NavBar from "./components/NavBar.jsx";
import Login from "./pages/Login.jsx";
import RecipeListPage from "./pages/Recipe.jsx";
import RecipeDetails from "./pages/RecipeDetails.jsx";
import AddRecipe from "./pages/AddRecipe.jsx";
import RequireAuth from "./components/RequireAuth.jsx";
import AddMealPlan from "./pages/AddMealPlan.jsx";
import MealPlans from "./pages/MealPlans.jsx";
import MealPlanDetails from "./pages/MealPlanDetails.jsx";

export default function App() {
  return (
    <BrowserRouter>
      <NavBar />
      <div style={{marginLeft: "220px", padding: "20px" }}>
      <Routes>
        {/* Login Route */}
        <Route path="/login" element={<Login />} />
        {/* Recipe Route */}
        <Route path="/recipes" element={ <RequireAuth><RecipeListPage /></RequireAuth>} />
        {/* Detail page for specific recipe item */}
        <Route path="/recipes/:id" element={<RecipeDetails />} />
        {/* Add Recipe Route */}
        <Route path="/add-recipe" element={ <RequireAuth> <AddRecipe /></RequireAuth>} />
        {/* Add MealPlan Route */}
        <Route path="/add-mealPlan" element={ <RequireAuth> <AddMealPlan /></RequireAuth>} />
        {/* Meal Plan Route */}
        <Route path="/mealPlans" element={ <RequireAuth> <MealPlans /></RequireAuth>} />
        {/* Detail page for specific meal plan item */}
        <Route path="/mealPlans/:id" element={<MealPlanDetails />} />



        {/* Edit Recipe Route */}
        {/* <Route path="/edit-recipe" element={ <RequireAuth> <EditRecipe /></RequireAuth>} />
        {/* Delete Recipe Route */}
        {/* <Route path="/delete-recipe" element={ <RequireAuth> <RemoveRecipe /></RequireAuth>} /> */}
      </Routes>
      </div>
    </BrowserRouter>
  );
}
