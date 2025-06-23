package mx.edu.uttt.games.dto

data class UpdateGameDto(
    val name: String,
    val description: String,
    val minutes: Int
)