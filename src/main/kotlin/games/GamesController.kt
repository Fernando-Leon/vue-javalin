package mx.edu.uttt.games

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.games.dto.CreateGameDto
import mx.edu.uttt.games.dto.UpdateGameDto
import mx.edu.uttt.Util.properTrim
import java.util.concurrent.CompletableFuture.supplyAsync

object GamesController: CrudHandler {

    override fun create(ctx: Context) {
        ctx.bodyValidator<CreateGameDto>()
        .check({ it.name.isNotBlank() }, "Name cannot be blank")
        .check({ it.description.isNotBlank() }, "Description cannot be blank")
        .check({ it.minutes > 0 }, "Minutes must be positive")
        .get().apply {
            name = name.properTrim()
        }.also { game -> 
            ctx.future { supplyAsync { GamesService.create(game) }.thenAccept(ctx::result) }
        }
    }

    override fun delete(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { GamesService.delete(resourceId) }.thenAccept(ctx::result) }
    }

    override fun getAll(ctx: Context) {
        ctx.future { supplyAsync { GamesService.selectAll() }.thenAccept(ctx::json) }
    }

    override fun getOne(ctx: Context, resourceId: String) {
        ctx.future { supplyAsync { GamesService.selectById(resourceId) }.thenAccept(ctx::json) }
    }

    override fun update(ctx: Context, resourceId: String) {
        val updateGameDto = ctx.bodyValidator<UpdateGameDto>()
        .check({ it.name.isNotBlank() }, "Name cannot be blank")
        .check({ it.description.isNotBlank() }, "Description cannot be blank")
        .check({ it.minutes > 0 }, "Minutes must be positive")
        .get()
        ctx.future { supplyAsync { GamesService.update(resourceId, updateGameDto) }.thenAccept(ctx::result)}
    }
}