package mx.edu.uttt.mice.model

data class Mice(
    val id: String,
    val name: String,
    val description: String,
    val dpi: Int,
    val buttons: Int,
    val weight: Int,
    val wireless: Boolean,
    val price: Double
)