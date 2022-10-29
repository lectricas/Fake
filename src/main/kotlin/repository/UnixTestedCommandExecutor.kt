package repository

import java.io.BufferedReader
import java.io.InputStreamReader

class UnixTestedCommandExecutor : CommandExecutor {
    override fun executeCommand(command: String): String {
        val commandLine = arrayOf("sh", "-c", command)
        val process = Runtime.getRuntime().exec(commandLine)
        val `in` = BufferedReader(InputStreamReader(process.inputStream))
        val builder = StringBuilder()
        while (true) {
            val line = `in`.readLine() ?: break
            builder.append(line)
            builder.append("\n")
        }
        return builder.toString()
    }

}