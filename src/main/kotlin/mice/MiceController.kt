package mx.edu.uttt.mice

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.mice.dto.CreateMiceDto
import mx.edu.uttt.mice.dto.UpdateMiceDto
import java.util.concurrent.CompletableFuture.supplyAsync

object MiceController: CrudHandler {

    override fun create(ctx: Context) {
        val createMiceDto = ctx.bodyValidator<CreateMiceDto>().get()
        ctx.future { supplyAsync { MiceService.create(createMiceDto) }.thenAccept(ctx::result) }
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
        val updateMiceDto = ctx.bodyValidator<UpdateMiceDto>().get()
        ctx.future { supplyAsync { MiceService.update(resourceId, updateMiceDto) }.thenAccept(ctx::result)}
    }
}