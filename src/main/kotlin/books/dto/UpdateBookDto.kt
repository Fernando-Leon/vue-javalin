package mx.edu.uttt.books.dto

data class UpdateBookDto(
    val title: String,
    val author: String,
    val genre: String,
    val year: Int,
    val pages: Int
)