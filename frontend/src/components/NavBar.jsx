/**
 * Fixed, left-side navigation bar for Meal Planner App.
 * Links to pages, Displays title, etc.
 * @returns 
 */
import { useLocation } from "react-router-dom";

export default function NavBar() {
  
  // log current location
  const location = useLocation();
  const isActive = (path) => location.pathname === path;

  /**
   * Logs the user out of the frontend
   * TODO: This isn't working, check logic and add capability to
   * remove the "Login" button and only show "Logout button" when user
   * is logged in; vice-versa when user is logged out
   */
  const handleLogout = () => {
    localStorage.removeItem("alreadyLoggedIn");
    window.location.href = "/";
  };


  return (
    <aside style={styles.sidebar}>
      {/* Display title */}
      <div style={styles.logo}>Meal Planner</div>

      {/* Display Page Links */}
      <nav style={styles.nav}>
        {/* <a href="/login" style={styles.link}>Login</a> */}
        <a href="/login" style={{...styles.link,...(location.pathname === "/login" ? styles.activeLink : {})}}>
        Login
        </a>

        <a href="/recipes" style={{...styles.link,...(location.pathname === "/recipes" ? styles.activeLink : {})}}>
        My Recipes
        </a>

        <a href="/add-recipe" style={{...styles.link,...(location.pathname === "/add-recipe" ? styles.activeLink : {})}}>
        Add Recipe
        </a>

        <a href="/mealPlans" style={{...styles.link,...(location.pathname === "/mealPlans" ? styles.activeLink : {})}}>
        My Meal Plans
        </a>

        <a href="/add-mealPlan" style={{...styles.link,...(location.pathname === "/add-mealPlan" ? styles.activeLink : {})}}>
        Add Meal Plan
        </a>
        {/* <a href="/add-recipe" style={styles.link}>Add Food</a> */}
      </nav>

      {/* Display Logout button */}
      <div style={styles.logoutContainer}>
        <button onClick={handleLogout} style={styles.logoutBtn}>
          Logout
        </button>
      </div>
    </aside>
  );
}

// NavBar style
const styles = {
  sidebar: {
    width: "220px",
    height: "100vh",
    display: "flex",
    flexDirection: "column",
    borderRight: "1px solid #ddd",
    background: "#90c982",
    padding: "20px",
    gap: "30px",
    position: "fixed",
    top: 0,
    left: 0
  },
  logo: {
    fontSize: "1.4rem",
    fontWeight: "bold",
  },
  nav: {
    display: "flex",
    flexDirection: "column",
    gap: "20px"
  },
  link: {
    textDecoration: "none",
    color: "#333",
    fontWeight: "500",
    fontSize: "1rem"
  },
    activeLink: {
    borderLeft: "4px solid #2c662d",
    background: "rgba(255,255,255,0.3)",
    paddingLeft: "12px",
    fontWeight: "bold",
  },
  logoutContainer: {
    // TODO: This took the button out of view, fix next time
    // marginTop: "auto" // pushes logout button to bottom of sidebar
  },
  logoutBtn: {
    padding: "6px 12px",
    background: "#e74c3c",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
  }
};