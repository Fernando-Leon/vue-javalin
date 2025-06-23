package mx.edu.uttt.desserts.dto

data class UpdateDessertDto(
    val name: String,
    val calories: Int,
    val fat: Double,
    val carbs: Int,
    val protein: Double
)