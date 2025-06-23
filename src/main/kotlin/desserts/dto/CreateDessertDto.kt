package mx.edu.uttt.desserts.dto

data class CreateDessertDto(
    val name: String,
    val calories: Int,
    val fat: Double,
    val carbs: Int,
    val protein: Double
)