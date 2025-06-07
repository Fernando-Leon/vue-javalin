package mx.edu.uttt

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.http.staticfiles.Location
import io.javalin.vue.VueComponent

fun main() {
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
            ApiBuilder.get("/", VueComponent("home-page"))
            ApiBuilder.get("/popular-books", VueComponent("popular-books"))
            ApiBuilder.get("/intent", VueComponent("intent"))
            ApiBuilder.get("/mice-page", VueComponent("mice-page"))
            ApiBuilder.get("/dessert-page", VueComponent("dessert-page"))
            ApiBuilder.get("/games-page", VueComponent("games-page"))
        }
    }.start();
}