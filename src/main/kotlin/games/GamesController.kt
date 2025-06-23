package mx.edu.uttt.games

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import mx.edu.uttt.games.dto.CreateGameDto
import mx.edu.uttt.games.dto.UpdateGameDto
import java.util.concurrent.CompletableFuture.supplyAsync

object GamesController: CrudHandler {

    override fun create(ctx: Context) {
        val createGameDto = ctx.bodyValidator<CreateGameDto>().get()
        ctx.future { supplyAsync { GamesService.create(createGameDto) }.thenAccept(ctx::result) }
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
        val updateGameDto = ctx.bodyValidator<UpdateGameDto>().get()
        ctx.future { supplyAsync { GamesService.update(resourceId, updateGameDto) }.thenAccept(ctx::result)}
    }
}