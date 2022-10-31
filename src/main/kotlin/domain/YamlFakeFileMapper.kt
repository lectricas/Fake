package domain

class YamlFakeFileMapper : FakeFileMapper {

    companion object {
        private const val DEPENDENCIES = "dependencies"
        private const val TARGET = "target"
        private const val RUN = "run"
    }

    override fun mapParsedFileToTasks(parsedYaml: Map<String, Any>): Map<String, Task> {
        return parsedYaml.mapValues { entry ->
            val taskMap = (entry.value as Map<String, Any>)
            val dependencyList = if (taskMap[DEPENDENCIES] == null) {
                listOf()
            } else {
                taskMap[DEPENDENCIES] as List<String>
            }

            val targetFilename = if (taskMap[TARGET] == null) {
                throw FakeFileWrongFormat(TARGET)
            } else {
                taskMap[TARGET] as String
            }

            val command = if (taskMap[RUN] == null) {
                throw FakeFileWrongFormat(RUN)
            } else {
                taskMap[RUN] as String
            }

            return@mapValues Task(
                taskName = entry.key,
                dependencyList = dependencyList,
                targetFilename = targetFilename,
                command = command
            )
        }
    }
}