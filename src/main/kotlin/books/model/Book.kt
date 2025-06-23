package mx.edu.uttt.books.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val genre: String,
    val year: Int,
    val pages: Int
)
