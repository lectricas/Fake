package repository

interface CommandExecutor {
    fun executeCommand(command: String): String
}