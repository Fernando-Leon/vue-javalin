package mx.edu.uttt.books

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.books.dto.CreateBookDto
import mx.edu.uttt.books.dto.UpdateBookDto
import java.util.concurrent.CompletableFuture.supplyAsync

object BookController: CrudHandler {

    override fun create(ctx: Context) {
        val createBookDto = ctx.bodyValidator<CreateBookDto>().get()
        ctx.future { supplyAsync { BookService.create(createBookDto) }.thenAccept(ctx::result) }
    }

    override fun delete(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { BookService.delete(resourceId) }.thenAccept(ctx::result) }
    }

    override fun getAll(ctx: Context) {
        ctx.future { supplyAsync { BookService.selectAll() }.thenAccept(ctx::json) }
    }

    override fun getOne(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { BookService.selectById(resourceId) }.thenAccept(ctx::json) }
    }

    override fun update(ctx: Context, resourceId: String) {
        val updateBookDto = ctx.bodyValidator<UpdateBookDto>().get()
        ctx.future { supplyAsync { BookService.update(resourceId, updateBookDto) }.thenAccept(ctx::result)}
    }
}