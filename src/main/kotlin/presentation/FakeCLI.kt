package presentation

import domain.FakeFileRunner
import repository.UnixTestedCommandExecutor
import repository.YamlParser

class FakeCLI {
    fun main(arguments: List<String>) {
        val runner = FakeFileRunner(
            YamlParser("fakefile.yaml"),
            UnixTestedCommandExecutor()
        )
        var result: String
        try {
            result = arguments.joinToString("\n") { taskName -> runner.run(taskName) }
        } catch (exception: Exception) {
            result = exception.toString()
        }
        println(result)
    }
}

fun main(args: Array<String>) {
    val cli = FakeCLI()
    cli.main(args.toList())
}