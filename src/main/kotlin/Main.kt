package mx.edu.uttt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.crud
import io.javalin.http.staticfiles.Location
import io.javalin.vue.VueComponent
import kotliquery.HikariCP
import mx.edu.uttt.desserts.DessertController
import io.javalin.json.JavalinJackson
import mx.edu.uttt.books.BookController
import mx.edu.uttt.dessertsiterator.DessertIteratorController
import mx.edu.uttt.games.GamesController
import mx.edu.uttt.mice.MiceController

fun main() {
    // Configura el mapeador de Jackson para Kotlin
    // Comentario de prueba para auto-reload
    val mapper = jacksonObjectMapper()
    JavalinJackson(mapper)

    // Coneccion a la base de datos
    HikariCP.default(
        "jdbc:firebird://localhost:3050/desserts?encoding=UTF8", // Alias (configurado en databases.conf)
        "sysdba",
        "M4st3rK3y"
    )

    val app = Javalin.create { config ->
        config.staticFiles.apply {
            enableWebjars()
            add("public", Location.CLASSPATH)
        }
        
        config.vue.apply {
            vueInstanceNameInJs = "app"
            rootDirectory("/vue", Location.CLASSPATH)
        }

        /* Montar paginas */
        config.router.mount {
        }.apiBuilder {
            get("/", VueComponent("home-page"))
            get("/popular-books", VueComponent("popular-books"))
            get("/mice-page", VueComponent("mice-page"))
            get("/dessert-page", VueComponent("dessert-page"))
            get("/games-page", VueComponent("games-page"))
            get("/dessert-iterator-page", VueComponent("dessert-iterator-page"))
            path("api") {
                crud("desserts/{id}", DessertController) // Modulo de postres
                crud("books/{id}", BookController) // Modulo de Libros
                crud("dessert-iterator/{id}", DessertIteratorController) // Modulo de postres (iterator)
                crud("mice/{id}", MiceController) // Modulo de mouses
                crud("games/{id}", GamesController) // Modulo de juegos
            }
        }
    }.start();
}