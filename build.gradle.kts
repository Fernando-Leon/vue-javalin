plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "mx.edu.uttt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Webserver, Data Logger
    implementation("io.javalin:javalin:6.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

    /* Vue 3, Vuetify 3 & Material Design */
    implementation("org.webjars.npm:vue:3.5.17")
    implementation("org.webjars.npm:vuetify:3.7.0")
    implementation("org.webjars.npm:mdi__font:7.4.47")
    implementation("org.webjars:font-awesome:6.5.2")
    implementation("org.webjars.npm:roboto-fontface:0.10.0")

    /* Sweet Alert */
    implementation("org.webjars.npm:sweetalert2:11.11.1")

    /* Bcrypt */
    implementation("de.svenkubiak:jBCrypt:0.4.3")

    /* Database */
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("org.firebirdsql.jdbc:jaybird:5.0.5.java11")
    implementation("com.github.seratch:kotliquery:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("mx.edu.uttt.MainKt")
}

// Configuración para desarrollo con auto-recompilación
tasks.named<JavaExec>("run") {
    // Permitir entrada desde consola durante desarrollo
    standardInput = System.`in`
    
    // Configurar JVM args para desarrollo
    jvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-Djava.awt.headless=true"
    )
}