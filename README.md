# SWENG 861: Capstone Project
* **Project Name:** Meal Plan Application
* **Project Description:** This application allows the user to create custom recipes, add ingredients, save and view recipes, create meal plans, and add recipes to specific dates and meal types.
* **Tech Stack** 
*Backend:* Java + Maven, PostgreSQL, Google Auth, Docker
*Frontend:* React + Vite, JavaScript, Docker
* **Project Description:** Meal Plan application that allows users to add recipes, create meal plans, calculate nutritional values, and create grocery lists in one place. 

### File Structure - Backend
* **controllers** Handle the HTTP requests
* **models** Business logic
* **services** Database access

### File Structure - Frontend
* **components** reusable UI pieces 
* **pages** route-level screens
* **hooks** custom logic
* **services** API calls, helpers
### Dependencies
* Java 21
* Maven
* Node.js + npm
* PostgreSQL
* Docker (not fully implemented, in the future)

### Environment variables
* Create a .env.local file in /backend:
DB_URL=jdbc:postgresql://localhost:5432/mealplan
DB_USER=your_username
DB_PASSWORD=your_password

GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:8080/api/auth/callback
SESSION_SECRET=any_random_string

### Google Auth - not pushed to repo
serviceAccountKey.json

### Database Setup
* food: not used in current implementation
* meal_plans: id (PK integer), owner_id (text), name (text), start_date (date), end_date (date)
* meal_plan_entries:  id (PK integer), meal_plan_id (integer), recipe_id (integer), date (date), meal_type (text)
* recipes: id (PK integer), owner_id (text), name (text), description (text), instructions (text)
* recipe_ingredients: id (PK integer), recipe_id (integer), ingredient_name (text), quantity (text), unit (text)

### How to run - local
*Backend:*
* mvn clean install
* mvn exec:java
--> starts on http://localhost:8080

*Frontend:*
* npm install
* npm run dev
--> starts on http://localhost:5173


