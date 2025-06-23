package mx.edu.uttt.games

import io.javalin.http.BadRequestResponse
import io.javalin.http.InternalServerErrorResponse
import kotliquery.HikariCP
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import mx.edu.uttt.games.dto.CreateGameDto
import mx.edu.uttt.games.dto.UpdateGameDto
import mx.edu.uttt.games.model.Game
import java.sql.SQLException

object GamesService {

    private fun rowToGames(row: Row) = Game(
        id = row.string("ID"),
        name = row.string("DNAME"),
        description = row.string("DESCRIPTION"),
        minutes = row.int("MINUTES")
    )

    fun selectAll(): List<Game> {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, DNAME, DESCRIPTION, MINUTES
            FROM GAMES
            """.trimIndent()
        ).map(::rowToGames).asList

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry)
        }
    }

    fun selectById(id: String): Game {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, DNAME, DESCRIPTION, MINUTES
            FROM GAMES 
            WHERE ID = CHAR_TO_UUID(?)  
            """.trimIndent(),
            id
        ).map(::rowToGames).asSingle

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry) ?: throw InternalServerErrorResponse("No existe el elemento con ese id")
        }
    }

    fun create(createGameDto: CreateGameDto): String {
        val generatedId = java.util.UUID.randomUUID().toString()
        val qry = queryOf(
            """
            INSERT INTO GAMES (ID, DNAME, DESCRIPTION, MINUTES)
            VALUES (CHAR_TO_UUID(?), ?, ?, ?)
            """.trimIndent(),
            generatedId,
            createGameDto.name,
            createGameDto.description,
            createGameDto.minutes
        )

        try {
            sessionOf(HikariCP.dataSource()).use { conn ->
                val rows = conn.run(qry.asUpdate)
                if (rows > 0) return generatedId
                throw InternalServerErrorResponse("No se pudo ingresar")
            }
        } catch (e: SQLException) {
            if (e.message?.contains("DUPLICATED_ITEM") == true ||
                e.message?.contains("nombre del elemento ya existe", ignoreCase = true) == true
            ) {
                throw BadRequestResponse("Ya existe un juego con ese nombre")
            }
            throw InternalServerErrorResponse("Error inesperado: ${e.message}")
        }
    }

    fun delete(id: String): String {
        val checkQuery = queryOf(
            """
            SELECT COUNT(*) 
            FROM GAMES 
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        ).map { it.int(1) }.asSingle

        val qry = queryOf(
            """
            DELETE FROM GAMES
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        )

        try {
            sessionOf(HikariCP.dataSource()).use { conn ->
                val exists = conn.run(checkQuery) ?: 0
                if (exists == 0) {
                    return "No se encontró un juego con el ID proporcionado."
                }

                val rowsAffected = conn.run(qry.asUpdate)
                return if (rowsAffected > 0) {
                    "Juego eliminado exitosamente: $id"
                } else {
                    "Ocurrió un error al eliminar el juego."
                }
            }
        } catch (ex: Exception) {
            return "Error inesperado: ${ex.message}"
        }
    }

    fun update(id: String, updateGameDto: UpdateGameDto): String {
        val checkQuery = queryOf(
            "SELECT COUNT(*) FROM GAMES WHERE ID = CHAR_TO_UUID(?)",
            id
        ).map { it.int(1) }.asSingle

        val updateQuery = queryOf(
            """
            UPDATE GAMES 
            SET DNAME = ?, DESCRIPTION = ?, MINUTES = ?
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            updateGameDto.name,
            updateGameDto.description,
            updateGameDto.minutes,
            id
        )

        sessionOf(HikariCP.dataSource()).use { conn ->
            val exists = conn.run(checkQuery) ?: 0
            if (exists == 0) throw InternalServerErrorResponse("No se encontró el juego con ID $id")

            val updatedRows = conn.run(updateQuery.asUpdate)
            if (updatedRows > 0) {
                return "Juego actualizado exitosamente"
            } else {
                throw InternalServerErrorResponse("No se pudo actualizar el juego.")
            }
        }
    }
}