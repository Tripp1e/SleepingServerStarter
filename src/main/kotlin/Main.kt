package com.trivaris

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

val sessionName = "serversession"
val checkCommand = listOf("sh", "-c", "screen -list | grep $sessionName")

fun main() {
    val port = 25580

    embeddedServer(Netty, port = port) {
        routing {
            get("/run_server") {
                launch(Dispatchers.IO) {
                    executeBashCommand()
                }

                val serverStatus = getServerStatus()
                val currentDate = Date()
                call.respondText("Server Status: $serverStatus\nCurrent Date: $currentDate", ContentType.Text.Plain)
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

fun getServerStatus(): String {
    return try {

        val checkProcess = ProcessBuilder(checkCommand)
            .redirectErrorStream(true)
            .start()

        val checkInput = BufferedReader(InputStreamReader(checkProcess.inputStream))

        if (checkInput.readLine() != null) {
            "Running"
        } else {
            "Not Running"
        }
    } catch (e: Exception) {
        "Error checking server status: ${e.message}"
    }
}
