const express = require("express");

const app = express();
const PORT = 3000;

// Allow Express to read JSON request bodies.
app.use(express.json());

// Simple endpoint used to check whether the API is running.
app.get("/health", (request, response) => {
    response.status(200).json({
        status: "UP"
    });
});

app.listen(PORT, () => {
    console.log(`Marketing Campaign API running on http://localhost:${PORT}`);
});