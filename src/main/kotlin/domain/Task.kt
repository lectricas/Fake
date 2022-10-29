package domain

data class Task(
    val taskName: String,
    val dependencyList: List<String>,
    val targetFilename: String,
    val command: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (taskName != other.taskName) return false

        return true
    }

    override fun hashCode(): Int {
        return taskName.hashCode()
    }
}