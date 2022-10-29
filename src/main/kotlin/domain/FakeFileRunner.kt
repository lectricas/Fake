package domain

import repository.CommandExecutor
import repository.FakeFileParser
import java.nio.file.Files
import java.nio.file.Paths


class FakeFileRunner(
    private val fakeFileParser: FakeFileParser,
    private val executor: CommandExecutor
) {
    fun run(taskName: String): String {
        val parsedYaml = fakeFileParser.parseDefaultFakeFile()
        val taskMap = FakeFileMapper.mapParsedFileToTasks(parsedYaml)
        checkAllDependenciesExist(taskMap)
        val stringMap = FakeFileMapper.mapTasksToStrings(taskMap)
        val graph = Graph(stringMap)
        val sortedTaskNames = graph.topSortFrom(taskName)
        return executeTasksWithOrder(taskMap, sortedTaskNames)
    }

    private fun checkAllDependenciesExist(mapped: Map<String, Task>) {
        mapped.forEach { (_, task) ->
            task.dependencyList.forEach { dependency ->
                val path = Paths.get(dependency)
                if (!Files.exists(path) && !mapped.containsKey(dependency)) {
                    throw RuleNotFound(dependency)
                }
            }
        }
    }

    private fun executeTasksWithOrder(tasks: Map<String, Task>, sortedTaskNames: List<String>): String {
        val builder = StringBuilder()
        sortedTaskNames.forEach { taskName ->
            val taskInOrder = tasks[taskName]!! // tasks must contain taskName key
            val result: String
            if (!isUpToDate(taskInOrder, tasks)) {
                result = executor.executeCommand(taskInOrder.command)
            } else {
                result = "Task ${taskInOrder.taskName} is up to date.\n"
            }
            builder.append(taskInOrder.command)
            builder.append("\n")
            builder.append(result)
        }
        return builder.toString()
    }

    private fun isUpToDate(task: Task, tasks: Map<String, Task>): Boolean {
        val targetPath = Paths.get(task.targetFilename)
        return task.dependencyList
            .map { dependencyName -> tasks[dependencyName] }
            .any { dependentTask ->
                if (Files.exists(targetPath)) {
                    val dependencyFile = Paths.get(dependentTask!!.targetFilename)
                    val dependencyTime = Files.getLastModifiedTime(dependencyFile)
                    val targetTime = Files.getLastModifiedTime(targetPath)
                    return@any dependencyTime > targetTime
                } else {
                    return false
                }
            }
    }
}