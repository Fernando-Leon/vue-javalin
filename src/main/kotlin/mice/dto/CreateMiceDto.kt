package mx.edu.uttt.mice.dto

data class CreateMiceDto(
    val name: String,
    val description: String,
    val dpi: Int,
    val buttons: Int,
    val weight: Int,
    val wireless: Boolean,
    val price: Double
)