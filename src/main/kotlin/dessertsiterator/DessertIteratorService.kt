package mx.edu.uttt.dessertsiterator

import io.javalin.http.BadRequestResponse
import io.javalin.http.InternalServerErrorResponse
import kotliquery.HikariCP
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import mx.edu.uttt.dessertsiterator.dto.CreateDessertIteratorDto
import mx.edu.uttt.dessertsiterator.dto.UpdateDessertIteratorDto
import mx.edu.uttt.dessertsiterator.model.DessertIterator
import java.sql.SQLException

object DessertIteratorService {

    private fun rowToDessertsIterator(row: Row) = DessertIterator(
        id = row.string("ID"),
        name = row.string("DNAME"),
        description = row.string("DESCRIPTION"),
        calories = row.int("CALORIES"),
        fat = row.double("FAT"),
        carbs = row.int("CARBS"),
        protein = row.double("PROTEIN"),
        sodium = row.int("SODIUM"),
        calcium = row.int("CALCIUM"),
        iron = row.int("IRON")
    )

    fun selectAll(): List<DessertIterator> {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, DNAME, DESCRIPTION, CALORIES, FAT, CARBS, PROTEIN, SODIUM, CALCIUM, IRON
            FROM DESSERTITERATORS
            """.trimIndent()
        ).map(::rowToDessertsIterator).asList

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry)
        }
    }

    fun selectById(id: String): DessertIterator {
        val qry = queryOf(
            """
            SELECT UUID_TO_CHAR(ID) ID, DNAME, DESCRIPTION, CALORIES, FAT, CARBS, PROTEIN, SODIUM, CALCIUM, IRON
            FROM DESSERTITERATORS 
            WHERE ID = CHAR_TO_UUID(?)  
            """.trimIndent(),
            id
        ).map(::rowToDessertsIterator).asSingle

        return sessionOf(HikariCP.dataSource()).use { conn ->
            conn.run(qry) ?: throw InternalServerErrorResponse("No existe el elemento con ese id")
        }
    }

    fun create(createDessertIteratorDto: CreateDessertIteratorDto): String {
        val generatedId = java.util.UUID.randomUUID().toString()
        val qry = queryOf(
            """
            INSERT INTO DESSERTITERATORS (ID, 
            DNAME, DESCRIPTION, CALORIES, FAT, CARBS, PROTEIN, SODIUM, CALCIUM, IRON)
            VALUES (CHAR_TO_UUID(?), ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            generatedId,
            createDessertIteratorDto.name,
            createDessertIteratorDto.description,
            createDessertIteratorDto.calories,
            createDessertIteratorDto.fat,
            createDessertIteratorDto.carbs,
            createDessertIteratorDto.protein,
            createDessertIteratorDto.sodium,
            createDessertIteratorDto.calcium,
            createDessertIteratorDto.iron
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
            FROM DESSERTITERATORS 
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            id
        ).map { it.int(1) }.asSingle

        val qry = queryOf(
            """
            DELETE FROM DESSERTITERATORS
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

    fun update(id: String, updateDessertIteratorDto: UpdateDessertIteratorDto): String {
        val checkQuery = queryOf(
            "SELECT COUNT(*) FROM DESSERTITERATORS WHERE ID = CHAR_TO_UUID(?)",
            id
        ).map { it.int(1) }.asSingle

        val updateQuery = queryOf(
            """
            UPDATE DESSERTITERATORS 
            SET DNAME = ?, DESCRIPTION = ?, CALORIES = ?, FAT = ?, CARBS = ?, PROTEIN = ?, SODIUM = ?, CALCIUM = ?, IRON = ? 
            WHERE ID = CHAR_TO_UUID(?)
            """.trimIndent(),
            updateDessertIteratorDto.name,
            updateDessertIteratorDto.description,
            updateDessertIteratorDto.calories,
            updateDessertIteratorDto.fat,
            updateDessertIteratorDto.carbs,
            updateDessertIteratorDto.protein,
            updateDessertIteratorDto.sodium,
            updateDessertIteratorDto.calcium,
            updateDessertIteratorDto.iron,
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
