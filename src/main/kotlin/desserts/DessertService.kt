package mx.edu.uttt.desserts

import io.javalin.http.BadRequestResponse
import io.javalin.http.InternalServerErrorResponse
import kotliquery.HikariCP
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import mx.edu.uttt.desserts.dto.CreateDessertDto
import mx.edu.uttt.desserts.dto.UpdateDessertDto
import mx.edu.uttt.desserts.model.Dessert
import java.sql.SQLException

object DessertService {

    private fun rowToDesserts(row: Row) = Dessert(
        id = row.string("ID"),
        name = row.string("DNAME"),
        calories = row.int("CALORIES"),
        fat = row.double("FAT"),
        carbs = row.int("CARBS"),
        protein = row.double("PROTEIN")
    )

    fun selectAll(): List<Dessert> {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, DNAME, CALORIES, FAT, CARBS, PROTEIN
            FROM DESSERT
            """.trimIndent()
        ).map(::rowToDesserts).asList

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry)
        }
    }

    fun selectById(id: String): Dessert {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, DNAME, CALORIES, FAT, CARBS, PROTEIN
            FROM DESSERT 
            WHERE ID = CHAR_TO_UUID(?)  
            """.trimIndent(),
            id
        ).map(::rowToDesserts).asSingle

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry) ?: throw InternalServerErrorResponse("No existe el elemento con ese id")
        }
    }

    fun create(createDessertDto: CreateDessertDto): String {
        val generatedId = java.util.UUID.randomUUID().toString()
        val qry = queryOf(
            """
            INSERT INTO DESSERT (ID, DNAME, CALORIES, FAT, CARBS, PROTEIN)
            VALUES (CHAR_TO_UUID(?), ?, ?, ?, ?, ?)
            """.trimIndent(),
            generatedId,
            createDessertDto.name,
            createDessertDto.calories,
            createDessertDto.fat,
            createDessertDto.carbs,
            createDessertDto.protein
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
                throw BadRequestResponse("Ya existe un postre con ese nombre")
            }
            throw InternalServerErrorResponse("Error inesperado: ${e.message}")
        }
    }

    fun delete(id: String): String {
        val checkQuery = queryOf(
            """
            SELECT COUNT(*) 
            FROM DESSERT 
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        ).map { it.int(1) }.asSingle

        val qry = queryOf(
            """
            DELETE FROM DESSERT
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        )

        try {
            sessionOf(HikariCP.dataSource()).use { conn ->
                val exists = conn.run(checkQuery) ?: 0
                if (exists == 0) {
                    return "No se encontró un postre con el ID proporcionado."
                }

                val rowsAffected = conn.run(qry.asUpdate)
                return if (rowsAffected > 0) {
                    "Postre eliminado exitosamente: $id"
                } else {
                    "Ocurrió un error al eliminar el postre."
                }
            }
        } catch (ex: Exception) {
            return "Error inesperado: ${ex.message}"
        }
    }

    fun update(id: String, updateDessertDto: UpdateDessertDto): String {
        val checkQuery = queryOf(
            "SELECT COUNT(*) FROM DESSERT WHERE ID = CHAR_TO_UUID(?)",
            id
        ).map { it.int(1) }.asSingle

        val updateQuery = queryOf(
            """
            UPDATE DESSERT 
            SET DNAME = ?, CALORIES = ?, FAT = ?, CARBS = ?, PROTEIN = ?
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            updateDessertDto.name,
            updateDessertDto.calories,
            updateDessertDto.fat,
            updateDessertDto.carbs,
            updateDessertDto.protein,
            id
        )

        sessionOf(HikariCP.dataSource()).use { conn ->
            val exists = conn.run(checkQuery) ?: 0
            if (exists == 0) throw InternalServerErrorResponse("No se encontró el postre con ID $id")

            val updatedRows = conn.run(updateQuery.asUpdate)
            if (updatedRows > 0) {
                return "Postre actualizado exitosamente"
            } else {
                throw InternalServerErrorResponse("No se pudo actualizar el postre.")
            }
        }
    }
}
