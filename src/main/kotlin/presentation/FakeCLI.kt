package presentation

import domain.*
import repository.FileDependencyChecker
import repository.ShellCommandExecutor
import repository.YamlParser

class FakeCLI {
    fun main(arguments: List<String>) {
        if (arguments.isEmpty()) {
            println("Usage: fake [taskname]")
            return
        }

        val runner = FakeTaskRunner(
            YamlParser("fakefile.yaml"),
            ShellCommandExecutor(),
            FileDependencyChecker(),
            YamlFakeFileMapper()
        )
        var result: String
        try {
            result = arguments.joinToString("\n") { taskName -> runner.run(taskName) }
        } catch (exception: Exception) {
            result = when (exception) {
                is RuleNotFound -> {
                    "Rule not found ${exception.message}"
                }
                is CycleException -> {
                    "Cycle in your dependencies: ${exception.message}"
                }
                is FakeFileNotFound -> {
                    "FakeFile not found: ${exception.message}"
                }
                is FakeFileWrongFormat -> {
                    "FakeFile wrong format ${exception.message}"
                }
                else -> {
                    exception.toString()
                }
            }
        }
        print(result)
    }
}

fun main(args: Array<String>) {
    val cli = FakeCLI()
    cli.main(args.toList())
}