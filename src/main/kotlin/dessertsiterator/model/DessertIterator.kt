package mx.edu.uttt.dessertsiterator.model

data class DessertIterator(
    val id: String,
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
