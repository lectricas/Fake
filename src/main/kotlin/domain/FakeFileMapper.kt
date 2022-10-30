package domain

interface FakeFileMapper {
    fun mapParsedFileToTasks(parsedYaml: Map<String, Any>): Map<String, Task>
}