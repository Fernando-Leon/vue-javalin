package mx.edu.uttt.dessertsiterator.dto

data class UpdateDessertIteratorDto(
    val name: String,
    val description: String,
    val calories: Int,
    val fat: Double,
    val carbs: Int,
    val protein: Double,
    val sodium: Int,
    val calcium: Int,
    val iron: Int
)