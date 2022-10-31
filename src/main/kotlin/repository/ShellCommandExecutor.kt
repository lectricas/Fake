package repository

import java.io.BufferedReader
import java.io.InputStreamReader

class ShellCommandExecutor : CommandExecutor {
    override fun executeCommand(command: String): String {
        val commandLine = arrayOf("sh", "-c", command)
        val process = Runtime.getRuntime().exec(commandLine)
        val bufferReader = BufferedReader(InputStreamReader(process.inputStream))
        val builder = StringBuilder()
        while (true) {
            val line = bufferReader.readLine() ?: break
            builder.append(line)
            builder.append("\n")
        }
        return builder.toString()
    }
}