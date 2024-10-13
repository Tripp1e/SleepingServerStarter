package com.trivaris

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlinx.html.*

val sessionName = "serversession"
val checkCommand = listOf("sh", "-c", "screen -list | grep $sessionName")

fun main() {
    val port = 25580

    embeddedServer(Netty, port = port) {
        routing {
            get("/") {
                val action = call.parameters["action"]

                if (action == "start") {
                    launch(Dispatchers.IO) {
                        executeBashCommand()
                    }

                    // Redirect to /run_server after starting the server
                    call.respondRedirect("/")
                    return@get // Exit the function after redirecting
                }

                val currentDate = Date()
                val serverRunning = isServerRunning()
                call.respondHtml {
                    body {
                        h1 { +"Server Status: ${if (serverRunning)  "Running" else "Not Running"}" }
                        p { +"Current Date: $currentDate" }

                        if (!serverRunning) {
                            a(href = "/run_server?action=start") {
                                button(type = ButtonType.submit) {
                                    +"Start Server"
                                }
                            }
                        }
                    }
                }
            }

        }
    }.start(wait = true)
}

fun executeBashCommand() {
    try {
        val checkProcess = ProcessBuilder(checkCommand)
            .redirectErrorStream(true)
            .start()

        val checkInput = BufferedReader(InputStreamReader(checkProcess.inputStream))

        if (checkInput.readLine() != null) {
            println("Server is already running in screen session '$sessionName'.")
            return
        }

        // Command to start a new screen session and run the script
        val command = listOf("sh", "-c", "screen -dmS $sessionName sh start.sh")
        println("Starting new server session...")

        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        process.waitFor()
    } catch (e: Exception) {
        println("Error executing command: ${e.message}")
    }
}

fun isServerRunning(): Boolean {
    return try {
        val checkProcess = ProcessBuilder(checkCommand)
            .redirectErrorStream(true)
            .start()

        val checkInput = BufferedReader(InputStreamReader(checkProcess.inputStream))

        if (checkInput.readLine() != null) {
            true
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}
