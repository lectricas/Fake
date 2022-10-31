package domain

import repository.CommandExecutor
import repository.DependencyChecker
import repository.FakeFileParser


class FakeTaskRunner(
    private val fakeFileParser: FakeFileParser,
    private val executor: CommandExecutor,
    private val dependencyChecker: DependencyChecker,
    private val yamlFileMapper: FakeFileMapper
) {
    fun run(taskName: String): String {
        val parsedYaml = fakeFileParser.parseDefaultFakeFile()
        val taskMap = yamlFileMapper.mapParsedFileToTasks(parsedYaml)
        checkAllDependenciesExist(taskMap)
        val stringMap = mapTasksToStrings(taskMap)
        val graph = Graph(stringMap)
        val sortedTaskNames = graph.topSortFrom(taskName)
        return executeTasksInOrder(taskMap, sortedTaskNames)
    }

    //TODO check dependencies while execution
    private fun checkAllDependenciesExist(mapped: Map<String, Task>) {
        mapped.forEach { (_, task) ->
            task.dependencyList.forEach { dependency ->
                if (!dependencyChecker.exists(dependency) && !mapped.containsKey(dependency)) {
                    throw RuleNotFound(dependency)
                }
            }
        }
    }

    private fun executeTasksInOrder(tasks: Map<String, Task>, sortedTaskNames: List<String>): String {
        val builder = StringBuilder()
        sortedTaskNames.forEach { taskName ->
            val taskInOrder = tasks.getOrElse(taskName) { throw RuleNotFound(taskName) }
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

    private fun isUpToDate(targetTask: Task, allTasks: Map<String, Task>): Boolean {
        return targetTask.dependencyList
            .map { dependencyName ->
                val temp = allTasks[dependencyName]
                if (temp == null) {
                    return@map dependencyName
                } else {
                    return@map temp.targetFilename
                }
            }
            .all { dependentTaskFilename ->
                if (dependencyChecker.exists(targetTask.targetFilename)) {
                    return dependencyChecker.isGreater(
                        targetTask.targetFilename,
                        dependentTaskFilename
                    )
                } else {
                    return false
                }
            } && targetTask.dependencyList.isNotEmpty()
    }

    private fun mapTasksToStrings(mapped: Map<String, Task>): Map<String, List<String>> {
        return mapped.mapValues {
            return@mapValues it.value.dependencyList
        }
    }
}