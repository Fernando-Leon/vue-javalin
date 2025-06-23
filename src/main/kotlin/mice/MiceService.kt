package mx.edu.uttt.mice

import io.javalin.http.BadRequestResponse
import io.javalin.http.InternalServerErrorResponse
import kotliquery.HikariCP
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import mx.edu.uttt.mice.dto.CreateMiceDto
import mx.edu.uttt.mice.dto.UpdateMiceDto
import mx.edu.uttt.mice.model.Mice
import java.sql.SQLException

object MiceService {

    private fun rowToMice(row: Row) = Mice(
        id = row.string("ID"),
        name = row.string("DNAME"),
        description = row.string("DESCRIPTION"),
        dpi = row.int("DPI"),
        buttons = row.int("BUTTONS"),
        weight = row.int("WEIGHT"),
        wireless = row.boolean("WIRELESS"),
        price =  row.double("PRICE")
    )

    fun selectAll(): List<Mice> {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, DNAME, DESCRIPTION, DPI, BUTTONS, WEIGHT, WIRELESS, PRICE
            FROM MICE
            """.trimIndent()
        ).map(::rowToMice).asList

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry)
        }
    }

    fun selectById(id: String): Mice {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, DNAME, DESCRIPTION, DPI, BUTTONS, WEIGHT, WIRELESS, PRICE
            FROM MICE 
            WHERE ID = CHAR_TO_UUID(?)  
            """.trimIndent(),
            id
        ).map(::rowToMice).asSingle

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry) ?: throw InternalServerErrorResponse("No existe el elemento con ese id")
        }
    }

    fun create(createMiceDto: CreateMiceDto): String {
        val generatedId = java.util.UUID.randomUUID().toString()
        val qry = queryOf(
            """
            INSERT INTO MICE (ID, DNAME, DESCRIPTION, DPI, BUTTONS, WEIGHT, WIRELESS, PRICE)
            VALUES (CHAR_TO_UUID(?), ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            generatedId,
            createMiceDto.name,
            createMiceDto.description,
            createMiceDto.dpi,
            createMiceDto.buttons,
            createMiceDto.weight,
            createMiceDto.wireless,
            createMiceDto.price
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
                throw BadRequestResponse("Ya existe un mouse con ese nombre")
            }
            throw InternalServerErrorResponse("Error inesperado: ${e.message}")
        }
    }

    fun delete(id: String): String {
        val checkQuery = queryOf(
            """
            SELECT COUNT(*) 
            FROM MICE 
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        ).map { it.int(1) }.asSingle

        val qry = queryOf(
            """
            DELETE FROM MICE
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        )

        try {
            sessionOf(HikariCP.dataSource()).use { conn ->
                val exists = conn.run(checkQuery) ?: 0
                if (exists == 0) {
                    return "No se encontró un mouse con el ID proporcionado."
                }

                val rowsAffected = conn.run(qry.asUpdate)
                return if (rowsAffected > 0) {
                    "Mouse eliminado exitosamente: $id"
                } else {
                    "Ocurrió un error al eliminar el mouse."
                }
            }
        } catch (ex: Exception) {
            return "Error inesperado: ${ex.message}"
        }
    }

    fun update(id: String, updateMiceDto: UpdateMiceDto): String {
        val checkQuery = queryOf(
            "SELECT COUNT(*) FROM MICE WHERE ID = CHAR_TO_UUID(?)",
            id
        ).map { it.int(1) }.asSingle

        val updateQuery = queryOf(
            """
            UPDATE MICE 
            SET DNAME = ?, DESCRIPTION = ?, DPI = ?, BUTTONS = ?, WEIGHT = ?, WIRELESS = ?, PRICE= ?
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            updateMiceDto.name,
            updateMiceDto.description,
            updateMiceDto.dpi,
            updateMiceDto.buttons,
            updateMiceDto.weight,
            updateMiceDto.wireless,
            updateMiceDto.price,
            id
        )

        sessionOf(HikariCP.dataSource()).use { conn ->
            val exists = conn.run(checkQuery) ?: 0
            if (exists == 0) throw InternalServerErrorResponse("No se encontró el mouse con ID $id")

            val updatedRows = conn.run(updateQuery.asUpdate)
            if (updatedRows > 0) {
                return "Mouse actualizado exitosamente"
            } else {
                throw InternalServerErrorResponse("No se pudo actualizar el mouse.")
            }
        }
    }
}
