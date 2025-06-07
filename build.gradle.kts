plugins {
    kotlin("jvm") version "2.1.20"
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

    /* Vue 3, Vuetify 3 & Material Design */
    implementation("org.webjars.npm:vue:3.5.4")
    implementation("org.webjars.npm:vuetify:3.7.0")
    implementation("org.webjars.npm:mdi__font:7.4.47")
    implementation("org.webjars:font-awesome:6.5.2")
    implementation("org.webjars.npm:roboto-fontface:0.10.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}