package mx.edu.uttt.desserts

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.desserts.dto.CreateDessertDto
import mx.edu.uttt.desserts.dto.UpdateDessertDto
import mx.edu.uttt.Util.properTrim
import java.util.concurrent.CompletableFuture.supplyAsync

object DessertController: CrudHandler {

    override fun create(ctx: Context) {
        ctx.bodyValidator<CreateDessertDto>()
        .check({ it.name.isNotBlank() }, "Name cannot be blank")
        .check({ it.calories >= 0 }, "Calories must be positive")
        .check({ it.fat >= 0 }, "Fat must be positive")
        .check({ it.carbs >= 0 }, "Carbs must be positive")
        .check({ it.protein >= 0 }, "Protein must be positive")
        .get().apply {
            name = name.properTrim()
        }.also { dessert -> 
            ctx.future { supplyAsync { DessertService.create(dessert) }.thenAccept(ctx::result) }
        }
    }

    override fun delete(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { DessertService.delete(resourceId) }.thenAccept(ctx::result) }
    }

    override fun getAll(ctx: Context) {
        ctx.future { supplyAsync { DessertService.selectAll() }.thenAccept(ctx::json) }
    }

    override fun getOne(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { DessertService.selectById(resourceId) }.thenAccept(ctx::json) }
    }

    override fun update(ctx: Context, resourceId: String) {
        val updateDessertDto = ctx.bodyValidator<UpdateDessertDto>()
        .check({ it.name.isNotBlank() }, "Name cannot be blank")
        .check({ it.calories >= 0 }, "Calories must be positive")
        .check({ it.fat >= 0 }, "Fat must be positive")
        .check({ it.carbs >= 0 }, "Carbs must be positive")
        .check({ it.protein >= 0 }, "Protein must be positive")
        .get()
        ctx.future { supplyAsync { DessertService.update(resourceId, updateDessertDto) }.thenAccept(ctx::result)}
    }
}