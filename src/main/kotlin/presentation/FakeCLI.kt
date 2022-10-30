package presentation

import domain.YamlFakeFileMapper
import domain.FakeTaskRunner
import repository.FileDependencyChecker
import repository.UnixTestedCommandExecutor
import repository.YamlParser

class FakeCLI {
    fun main(arguments: List<String>) {
        if (arguments.isEmpty()) {
            println("Usage: fake [taskname]")
            return
        }

        val runner = FakeTaskRunner(
            YamlParser("fakefile.yaml"),
            UnixTestedCommandExecutor(),
            FileDependencyChecker(),
            YamlFakeFileMapper()
        )
        var result: String
        try {
            result = arguments.joinToString("\n") { taskName -> runner.run(taskName) }
        } catch (exception: Exception) {
            result = exception.toString()
        }
        print(result)
    }
}

fun main(args: Array<String>) {
    val cli = FakeCLI()
    cli.main(args.toList())
}