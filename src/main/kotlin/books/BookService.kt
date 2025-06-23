package mx.edu.uttt.books

import io.javalin.http.InternalServerErrorResponse
import kotliquery.HikariCP
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import mx.edu.uttt.books.dto.CreateBookDto
import mx.edu.uttt.books.dto.UpdateBookDto
import mx.edu.uttt.books.model.Book

object BookService {

    private fun rowToBooks(row: Row) = Book(
        id = row.string("ID"),
        title = row.string("TITLE"),
        author = row.string("AUTHOR"),
        genre = row.string("GENRE"),
        year = row.int("PUBLIC_YEAR"),
        pages = row.int("NUMNER_PAGES")
    )

    fun selectAll(): List<Book> {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, TITLE, AUTHOR, GENRE, PUBLIC_YEAR, NUMNER_PAGES
            FROM BOOKS
            """.trimIndent()
        ).map(::rowToBooks).asList

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry)
        }
    }

    fun selectById(id: String): Book {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, TITLE, AUTHOR, GENRE, PUBLIC_YEAR, NUMNER_PAGES
            FROM BOOKS 
            WHERE ID = CHAR_TO_UUID(?)  
            """.trimIndent(),
            id
        ).map(::rowToBooks).asSingle

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry) ?: throw InternalServerErrorResponse("No existe el elemento con ese id")
        }
    }

    fun create(createBookDto: CreateBookDto): String {
        val generatedId = java.util.UUID.randomUUID().toString()
        val qry = queryOf(
            """
            INSERT INTO BOOKS (ID, TITLE, AUTHOR, GENRE, PUBLIC_YEAR, NUMNER_PAGES)
            VALUES (CHAR_TO_UUID(?), ?, ?, ?, ?, ?)
            """.trimIndent(),
            generatedId,
            createBookDto.title,
            createBookDto.author,
            createBookDto.genre,
            createBookDto.year,
            createBookDto.pages
        )

        var result = "fallo"

        sessionOf(HikariCP.dataSource()).use { conn ->
            result = if( conn.run(qry.asUpdate) > 0) "Se ingreso correctamente" else throw InternalServerErrorResponse("No se pudo ingresar")
        }
        return result
    }

    fun delete(id: String): String {
        val checkQuery = queryOf(
            """
            SELECT COUNT(*) 
            FROM BOOKS 
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        ).map { it.int(1) }.asSingle

        val qry = queryOf(
            """
            DELETE FROM BOOKS
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        )

        try {
            sessionOf(HikariCP.dataSource()).use { conn ->
                val exists = conn.run(checkQuery) ?: 0
                if (exists == 0) {
                    return "No se encontró el libro con el ID proporcionado."
                }

                val rowsAffected = conn.run(qry.asUpdate)
                return if (rowsAffected > 0) {
                    "Libro eliminado exitosamente: $id"
                } else {
                    "Ocurrió un error al eliminar el postre."
                }
            }
        } catch (ex: Exception) {
            return "Error inesperado: ${ex.message}"
        }
    }

    fun update(id: String, updateBookDto: UpdateBookDto): String {
        val checkQuery = queryOf(
            "SELECT COUNT(*) FROM BOOKS WHERE ID = CHAR_TO_UUID(?)",
            id
        ).map { it.int(1) }.asSingle

        val updateQuery = queryOf(
            """
            UPDATE BOOKS 
            SET TITLE = ?, AUTHOR = ?, GENRE = ?, PUBLIC_YEAR = ?, NUMNER_PAGES = ?
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            updateBookDto.title,
            updateBookDto.author,
            updateBookDto.genre,
            updateBookDto.year,
            updateBookDto.pages,
            id
        )

        sessionOf(HikariCP.dataSource()).use { conn ->
            val exists = conn.run(checkQuery) ?: 0
            if (exists == 0) throw InternalServerErrorResponse("No se encontró el libro con ID $id")

            val updatedRows = conn.run(updateQuery.asUpdate)
            if (updatedRows > 0) {
                return "Libro actualizado exitosamente"
            } else {
                throw InternalServerErrorResponse("No se pudo actualizar el libro.")
            }
        }
    }
}
