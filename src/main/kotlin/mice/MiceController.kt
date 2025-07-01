package mx.edu.uttt.mice

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.mice.dto.CreateMiceDto
import mx.edu.uttt.mice.dto.UpdateMiceDto
import mx.edu.uttt.Util.properTrim
import java.util.concurrent.CompletableFuture.supplyAsync

object MiceController: CrudHandler {

    override fun create(ctx: Context) {
        ctx.bodyValidator<CreateMiceDto>()
        .check({ it.name.isNotBlank() }, "Name cannot be blank")
        .check({ it.description.isNotBlank() }, "Description cannot be blank")
        .check({ it.dpi > 0 }, "DPI must be positive")
        .check({ it.buttons > 0 }, "Buttons must be positive")
        .check({ it.weight > 0 }, "Weight must be positive")
        .check({ it.price >= 0 }, "Price must be positive")
        .get().apply {
            name = name.properTrim()
        }.also { mice -> 
            ctx.future { supplyAsync { MiceService.create(mice) }.thenAccept(ctx::result) }
        }
    }

    override fun delete(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { MiceService.delete(resourceId) }.thenAccept(ctx::result) }
    }

    override fun getAll(ctx: Context) {
        ctx.future { supplyAsync { MiceService.selectAll() }.thenAccept(ctx::json) }
    }

    override fun getOne(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { MiceService.selectById(resourceId) }.thenAccept(ctx::json) }
    }

    override fun update(ctx: Context, resourceId: String) {
        val updateMiceDto = ctx.bodyValidator<UpdateMiceDto>()
        .check({ it.name.isNotBlank() }, "Name cannot be blank")
        .check({ it.description.isNotBlank() }, "Description cannot be blank")
        .check({ it.dpi > 0 }, "DPI must be positive")
        .check({ it.buttons > 0 }, "Buttons must be positive")
        .check({ it.weight > 0 }, "Weight must be positive")
        .check({ it.price >= 0 }, "Price must be positive")
        .get()
        ctx.future { supplyAsync { MiceService.update(resourceId, updateMiceDto) }.thenAccept(ctx::result)}
    }
}