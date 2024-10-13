plugins {
    kotlin("jvm") version "1.6.10"
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-netty:1.6.8")
    implementation("io.ktor:ktor-server-core:1.6.8")
    implementation("io.ktor:ktor-html-builder:1.6.8")
    implementation("ch.qos.logback:logback-classic:1.2.6")

    implementation("com.squareup.okhttp3:okhttp:4.9.2")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "com.trivaris.MainKt"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
