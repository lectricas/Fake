package domain

class YamlFakeFileMapper : FakeFileMapper {
    override fun mapParsedFileToTasks(parsedYaml: Map<String, Any>): Map<String, Task> {
        return parsedYaml.mapValues { entry ->
            val taskMap = (entry.value as Map<String, Any>)
            val dependencyList = if (taskMap["dependencies"] == null) {
                listOf()
            } else {
                taskMap["dependencies"] as List<String>
            }

            val targetFilename = if (taskMap["target"] == null) {
                throw FakeFileWrongFormat("Target not found")
            } else {
                taskMap["target"] as String
            }

            val command = if (taskMap["run"] == null) {
                throw FakeFileWrongFormat("run")
            } else {
                taskMap["run"] as String
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