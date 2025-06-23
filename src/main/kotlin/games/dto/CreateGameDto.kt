package mx.edu.uttt.games.dto

data class CreateGameDto(
    val name: String,
    val description: String,
    val minutes: Int
)
