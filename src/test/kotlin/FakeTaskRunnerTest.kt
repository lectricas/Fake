import domain.FakeFileMapper
import domain.FakeTaskRunner
import domain.Task
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import repository.CommandExecutor
import repository.DependencyChecker

class FakeTaskRunnerTest {
    @Test
    fun testSimple() {
        val task1 = "compile"
        val deps = listOf<String>()
        val targetName = "main.o"
        val run = "gcc -c main.c -o main.o"
        val commandResponse = "OK\n"
        val fileParser = mock<FakeFileMapper> {
            on { mapParsedFileToTasks(any()) } doReturn mapOf(
                Pair(
                    task1, Task(
                        taskName = task1,
                        dependencyList = deps,
                        targetFilename = targetName,
                        command = run
                    )
                )
            )
        }

        val executor = mock<CommandExecutor> {
            on { executeCommand(run) } doReturn commandResponse
        }

        val dependencyChecker = mock<DependencyChecker> {
            on { compareTime(any(), any()) } doReturn 0
            on { exists(any()) } doReturn false
        }

        val r = FakeTaskRunner(mock(), executor, dependencyChecker, fileParser)
        val actual = r.run(task1)
        assertEquals("$run\n$commandResponse", actual)
    }

    @Test
    fun testDependency() {
        val task1 = "compile"
        val deps = listOf("main.c")
        val targetName = "main.o"
        val run = "gcc -c main.c -o main.o"
        val commandResponse = "OK\n"
        val fileParser = mock<FakeFileMapper> {
            on { mapParsedFileToTasks(any()) } doReturn mapOf(
                Pair(
                    task1, Task(
                        taskName = task1,
                        dependencyList = deps,
                        targetFilename = targetName,
                        command = run
                    )
                )
            )
        }

        val executor = mock<CommandExecutor> {
            on { executeCommand(run) } doReturn commandResponse
        }

        val dependencyChecker = mock<DependencyChecker> {
            on { compareTime(targetName, deps.first()) } doReturn -1
            on { exists(deps.first()) } doReturn true
        }

        val r = FakeTaskRunner(mock(), executor, dependencyChecker, fileParser)
        val actual = r.run(task1)
        assertEquals("$run\n$commandResponse", actual)
    }

    @Test
    fun testUpToDate() {
        val task1 = "compile"
        val deps = listOf("main.c")
        val targetName = "main.o"
        val run = "gcc -c main.c -o main.o"
        val commandResponse = "OK\n"
        val fileParser = mock<FakeFileMapper> {
            on { mapParsedFileToTasks(any()) } doReturn mapOf(
                Pair(
                    task1, Task(
                        taskName = task1,
                        dependencyList = deps,
                        targetFilename = targetName,
                        command = run
                    )
                )
            )
        }

        val executor = mock<CommandExecutor> {
            on { executeCommand(run) } doReturn commandResponse
        }

        val dependencyChecker = mock<DependencyChecker> {
            on { exists(targetName) } doReturn true
            on { exists(deps.first()) } doReturn true
            on { compareTime(targetName, deps.first()) } doReturn 1
        }

        val r = FakeTaskRunner(mock(), executor, dependencyChecker, fileParser)
        val actual = r.run(task1)
        assertEquals("$run\nTask $task1 is up to date.\n", actual)
    }

    @Test
    fun testDependenciesTwoNode() {
        val task1 = "compile"
        val deps1 = listOf("main.c")
        val targetName1 = "main.o"
        val run1 = "gcc -c main.c -o main.o"
        val commandResponse1 = "OK1\n"

        val task2 = "build"
        val deps2 = listOf("compile")
        val targetName2 = "main"
        val run2 = "gcc main.o -o main"
        val commandResponse2 = "OK2\n"

        val fileParser = mock<FakeFileMapper> {
            on { mapParsedFileToTasks(any()) } doReturn mapOf(
                Pair(
                    task1, Task(
                        taskName = task1,
                        dependencyList = deps1,
                        targetFilename = targetName1,
                        command = run1
                    )
                ),
                Pair(
                    task2, Task(
                        taskName = task2,
                        dependencyList = deps2,
                        targetFilename = targetName2,
                        command = run2
                    )
                )
            )
        }

        val executor = mock<CommandExecutor> {
            on { executeCommand(run1) } doReturn commandResponse1
            on { executeCommand(run2) } doReturn commandResponse2
        }

        val dependencyChecker = mock<DependencyChecker> {
            on { compareTime(targetName1, deps1.first()) } doReturn -1
            on { compareTime(targetName2, deps2.first()) } doReturn -1
            on { exists(deps1.first()) } doReturn true
            on { exists(deps2.first()) } doReturn true
            on { exists(targetName1) } doReturn false
            on { exists(targetName2) } doReturn false
        }

        val r = FakeTaskRunner(mock(), executor, dependencyChecker, fileParser)
        val actual1 = r.run(task1)
        assertEquals("$run1\n$commandResponse1", actual1)

        val actual2 = r.run(task2)
        assertEquals("$run1\n$commandResponse1$run2\n$commandResponse2", actual2)
    }

    @Test
    fun testDependenciesTwoNodeFirstUpToDate() {
        val task1 = "compile"
        val deps1 = listOf("main.c")
        val targetName1 = "main.o"
        val run1 = "gcc -c main.c -o main.o"
        val commandResponse1 = "OK1\n"

        val task2 = "build"
        val deps2 = listOf("compile")
        val targetName2 = "main"
        val run2 = "gcc main.o -o main"
        val commandResponse2 = "OK2\n"

        val fileParser = mock<FakeFileMapper> {
            on { mapParsedFileToTasks(any()) } doReturn mapOf(
                Pair(
                    task1, Task(
                        taskName = task1,
                        dependencyList = deps1,
                        targetFilename = targetName1,
                        command = run1
                    )
                ),
                Pair(
                    task2, Task(
                        taskName = task2,
                        dependencyList = deps2,
                        targetFilename = targetName2,
                        command = run2
                    )
                )
            )
        }

        val executor = mock<CommandExecutor> {
            on { executeCommand(run1) } doReturn commandResponse1
            on { executeCommand(run2) } doReturn commandResponse2
        }

        val dependencyChecker = mock<DependencyChecker> {
            on { compareTime(targetName1, deps1.first()) } doReturn 1
            on { compareTime(targetName2, deps2.first()) } doReturn -1
            on { exists(deps1.first()) } doReturn true
            on { exists(deps2.first()) } doReturn true
            on { exists(targetName1) } doReturn true
            on { exists(targetName2) } doReturn false
        }

        val r = FakeTaskRunner(mock(), executor, dependencyChecker, fileParser)

        val actual2 = r.run(task2)
        assertEquals("$run2\n$commandResponse2", actual2)
    }

    @Test
    fun testDependenciesTwoNodeSecondUpToDate() {
        val task1 = "compile"
        val deps1 = listOf("main.c")
        val targetName1 = "main.o"
        val run1 = "gcc -c main.c -o main.o"
        val commandResponse1 = "OK1\n"

        val task2 = "build"
        val deps2 = listOf("compile")
        val targetName2 = "main"
        val run2 = "gcc main.o -o main"
        val commandResponse2 = "OK2\n"

        val fileParser = mock<FakeFileMapper> {
            on { mapParsedFileToTasks(any()) } doReturn mapOf(
                Pair(
                    task1, Task(
                        taskName = task1,
                        dependencyList = deps1,
                        targetFilename = targetName1,
                        command = run1
                    )
                ),
                Pair(
                    task2, Task(
                        taskName = task2,
                        dependencyList = deps2,
                        targetFilename = targetName2,
                        command = run2
                    )
                )
            )
        }

        val executor = mock<CommandExecutor> {
            on { executeCommand(run1) } doReturn commandResponse1
            on { executeCommand(run2) } doReturn commandResponse2
        }

        val dependencyChecker = mock<DependencyChecker> {
            on { compareTime(targetName1, deps1.first()) } doReturn 1
            on { compareTime(targetName2, targetName1) } doReturn 1
            on { exists(deps1.first()) } doReturn true
            on { exists(deps2.first()) } doReturn true
            on { exists(targetName1) } doReturn true
            on { exists(targetName2) } doReturn true
        }

        val r = FakeTaskRunner(mock(), executor, dependencyChecker, fileParser)

        val actual2 = r.run(task2)
        assertEquals("$run2\nTask $task2 is up to date.\n", actual2)
    }
}