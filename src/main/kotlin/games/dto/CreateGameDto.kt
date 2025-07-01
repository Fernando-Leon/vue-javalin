package mx.edu.uttt.games.dto

data class CreateGameDto(
    var name: String,
    val description: String,
    val minutes: Int
)
