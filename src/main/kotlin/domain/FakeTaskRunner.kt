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
        val skip = graph.topSortFrom(taskName)
        val resultBuilder = StringBuilder()
        executeTasksRecursively(taskMap, taskName, resultBuilder)
        return resultBuilder.toString()
    }

    private fun checkAllDependenciesExist(mapped: Map<String, Task>) {
        mapped.forEach { (_, task) ->
            task.dependencyList.forEach { dependency ->
                if (!dependencyChecker.exists(dependency) && !mapped.containsKey(dependency)) {
                    throw RuleNotFound(dependency)
                }
            }
        }
    }

    private fun executeTasksRecursively(taskMap: Map<String, Task>, from: String, resultBuilder: StringBuilder) {
        val current = taskMap[from]!!

        val isTargetFileExists = dependencyChecker.exists(current.targetFilename)
        var areAllDepsOlder = true
        current.dependencyList.forEach {
            val temp = taskMap[it]
            val filename = temp?.targetFilename ?: it
            if (!dependencyChecker.exists(filename)) {
                executeTasksRecursively(taskMap, it, resultBuilder)
            }

            if (isTargetFileExists) {
                areAllDepsOlder = areAllDepsOlder && dependencyChecker.compareTime(current.targetFilename, filename) > 0
            } else {
                areAllDepsOlder = false
            }
        }

        val isTaskUpToDate = current.dependencyList.isNotEmpty() && areAllDepsOlder

        val result = if (isTaskUpToDate) {
            "Task ${current.taskName} is up to date.\n"
        } else {
            executor.executeCommand(current.command)
        }
        resultBuilder.append(current.command)
        resultBuilder.append("\n")
        resultBuilder.append(result)
    }

//    private fun shouldExecuteCommand(targetTask: Task, allTasks: Map<String, Task>): Boolean {
//        return targetTask.dependencyList
//            .map { dependencyName ->
//                val temp = allTasks[dependencyName]
//                if (temp == null) {
//                    return@map dependencyName
//                } else {
//                    return@map temp.targetFilename
//                }
//            }
//            .any { dependentTaskFilename ->
//                if (dependencyChecker.exists(targetTask.targetFilename)) {
//                    return dependencyChecker.compareTime(
//                        targetTask.targetFilename,
//                        dependentTaskFilename
//                    ) > 0
//                } else {
//                    return false
//                }
//            }
//    }

//    private fun isUpToDate(targetTask: Task, allTasks: Map<String, Task>): Boolean {
//        return targetTask.dependencyList
//            .map { dependencyName ->
//                val temp = allTasks[dependencyName]
//                if (temp == null) {
//                    return@map dependencyName
//                } else {
//                    return@map temp.targetFilename
//                }
//            }
//            .any { dependentTaskFilename ->
//                if (dependencyChecker.exists(targetTask.targetFilename)) {
//                    return dependencyChecker.compareTime(
//                        targetTask.targetFilename,
//                        dependentTaskFilename
//                    ) > 0
//                } else {
//                    return false
//                }
//            }
//    }

    private fun mapTasksToStrings(mapped: Map<String, Task>): Map<String, List<String>> {
        return mapped.mapValues {
            return@mapValues it.value.dependencyList
        }
    }

    //    private fun executeTasksInReverseOrder(tasks: Map<String, Task>, sortedTaskNames: List<String>): String {
//        val builder = StringBuilder()
//        sortedTaskNames.reversed().forEach { taskName ->
//            val taskInOrder = tasks[taskName]!! // tasks must contain taskName key
//            val result: String
//            if (!isUpToDate(taskInOrder, tasks)) {
//                result = executor.executeCommand(taskInOrder.command)
//            } else {
//                result = "Task ${taskInOrder.taskName} is up to date.\n"
//            }
//            builder.append(taskInOrder.command)
//            builder.append("\n")
//            builder.append(result)
//        }
//        return builder.toString()
//    }
}