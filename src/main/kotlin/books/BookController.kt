package mx.edu.uttt.books

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.books.dto.CreateBookDto
import mx.edu.uttt.books.dto.UpdateBookDto
import mx.edu.uttt.books.BookService
import mx.edu.uttt.Util.properTrim
import java.util.concurrent.CompletableFuture.supplyAsync

object BookController: CrudHandler {

    override fun create(ctx: Context) {
        ctx.bodyValidator<CreateBookDto>()
        .check({ it.title.isNotBlank() }, "Title cannot be blank")
        .get().apply {
            title = title.properTrim()
        }.also { book -> 
            ctx.future { supplyAsync { BookService.create(book) }.thenAccept(ctx::result) }
        }
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