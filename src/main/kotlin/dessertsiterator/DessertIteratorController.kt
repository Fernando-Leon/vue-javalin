package mx.edu.uttt.dessertsiterator

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.dessertsiterator.dto.CreateDessertIteratorDto
import mx.edu.uttt.dessertsiterator.dto.UpdateDessertIteratorDto
import mx.edu.uttt.Util.properTrim
import java.util.concurrent.CompletableFuture.supplyAsync

object DessertIteratorController: CrudHandler {

    override fun create(ctx: Context) {
        ctx.bodyValidator<CreateDessertIteratorDto>()
        .check({ it.name.isNotBlank() }, "Name cannot be blank")
        .check({ it.description.isNotBlank() }, "Description cannot be blank")
        .check({ it.calories >= 0 }, "Calories must be positive")
        .check({ it.fat >= 0 }, "Fat must be positive")
        .check({ it.carbs >= 0 }, "Carbs must be positive")
        .check({ it.protein >= 0 }, "Protein must be positive")
        .check({ it.sodium >= 0 }, "Sodium must be positive")
        .check({ it.calcium >= 0 }, "Calcium must be positive")
        .check({ it.iron >= 0 }, "Iron must be positive")
        .get().apply {
            name = name.properTrim()
        }.also { dessert -> 
            ctx.future { supplyAsync { DessertIteratorService.create(dessert) }.thenAccept(ctx::result) }
        }
    }

    override fun delete(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { DessertIteratorService.delete(resourceId) }.thenAccept(ctx::result) }
    }

    override fun getAll(ctx: Context) {
        ctx.future { supplyAsync { DessertIteratorService.selectAll() }.thenAccept(ctx::json) }
    }

    override fun getOne(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { DessertIteratorService.selectById(resourceId) }.thenAccept(ctx::json) }
    }

    override fun update(ctx: Context, resourceId: String) {
        val updateDessertIteratorDto = ctx.bodyValidator<UpdateDessertIteratorDto>()
        .check({ it.name.isNotBlank() }, "Name cannot be blank")
        .check({ it.description.isNotBlank() }, "Description cannot be blank")
        .check({ it.calories >= 0 }, "Calories must be positive")
        .check({ it.fat >= 0 }, "Fat must be positive")
        .check({ it.carbs >= 0 }, "Carbs must be positive")
        .check({ it.protein >= 0 }, "Protein must be positive")
        .check({ it.sodium >= 0 }, "Sodium must be positive")
        .check({ it.calcium >= 0 }, "Calcium must be positive")
        .check({ it.iron >= 0 }, "Iron must be positive")
        .get()
        ctx.future { supplyAsync { DessertIteratorService.update(resourceId, updateDessertIteratorDto) }.thenAccept(ctx::result)}
    }
}