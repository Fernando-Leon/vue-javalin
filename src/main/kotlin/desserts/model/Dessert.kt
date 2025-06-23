package mx.edu.uttt.desserts.model

data class Dessert(
    val id: String,
    val name: String,
    val calories: Int,
    val fat: Double,
    val carbs: Int,
    val protein: Double
)