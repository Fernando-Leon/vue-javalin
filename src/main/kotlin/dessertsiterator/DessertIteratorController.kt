package mx.edu.uttt.dessertsiterator

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.dessertsiterator.dto.CreateDessertIteratorDto
import mx.edu.uttt.dessertsiterator.dto.UpdateDessertIteratorDto
import java.util.concurrent.CompletableFuture.supplyAsync

object DessertIteratorController: CrudHandler {

    override fun create(ctx: Context) {
        val createDessertIteratorDto = ctx.bodyValidator<CreateDessertIteratorDto>().get()
        ctx.future { supplyAsync { DessertIteratorService.create(createDessertIteratorDto) }.thenAccept(ctx::result) }
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
        val updateDessertIteratorDto = ctx.bodyValidator<UpdateDessertIteratorDto>().get()
        ctx.future { supplyAsync { DessertIteratorService.update(resourceId, updateDessertIteratorDto) }.thenAccept(ctx::result)}
    }
}