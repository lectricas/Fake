package unit

import domain.FakeFileMapper
import domain.FakeTaskRunner
import domain.Task
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import repository.CommandExecutor
import repository.DependencyChecker

class FakeTaskRunnerTest {

    private fun getUpToDate(taskName: String) = "Task $taskName is up to date.\n"

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
            on { isGreater(any(), any()) } doReturn true
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
            on { isGreater(targetName, deps.first()) } doReturn false
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
            on { isGreater(targetName, deps.first()) } doReturn true
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
            on { isGreater(targetName1, deps1.first()) } doReturn false
            on { isGreater(targetName2, deps2.first()) } doReturn false
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
    fun test_two_tasks_bamboo_leaf_modified_execute_all() {
        val taskA = "A"
        val depsA = listOf("C")
        val targetNameA = "A.o"
        val runA = "run_A"
        val commandResponseA = "OK_A\n"

        val taskB = "B"
        val depsB = listOf("A")
        val targetNameB = "B.o"
        val runB = "run_B"
        val commandResponseB = "OK_B\n"

        val fileParser = mock<FakeFileMapper> {
            on { mapParsedFileToTasks(any()) } doReturn mapOf(
                Pair(
                    taskA, Task(
                        taskName = taskA,
                        dependencyList = depsA,
                        targetFilename = targetNameA,
                        command = runA
                    )
                ),
                Pair(
                    taskB, Task(
                        taskName = taskB,
                        dependencyList = depsB,
                        targetFilename = targetNameB,
                        command = runB
                    )
                )
            )
        }

        var isCommandAExecuted = false

        val executor = mock<CommandExecutor> {
            on { executeCommand(runA) } doAnswer {
                isCommandAExecuted = true
                commandResponseA
            }
            on { executeCommand(runB) } doReturn commandResponseB
        }

        val dependencyChecker = mock<DependencyChecker> {
            on { isGreater(targetNameA, depsA.first()) } doReturn false
            on { isGreater(targetNameB, targetNameA) } doAnswer {
                !isCommandAExecuted // how to make this behavior out-of-box?
            }
            on { exists(depsA.first()) } doReturn true
            on { exists(depsB.first()) } doReturn true
            on { exists(targetNameA) } doReturn true
            on { exists(targetNameB) } doReturn true
        }

        val r = FakeTaskRunner(mock(), executor, dependencyChecker, fileParser)

        val actual2 = r.run(taskB)
        assertEquals("$runA\n$commandResponseA$runB\n$commandResponseB", actual2)
    }

    // cherry and bamboo are graph types
    @Test
    fun test_three_tasks_cherry_leaf_modified_execute_leaf_and_root() {
        val taskA = "A"
        val depsA = listOf("D")
        val targetNameA = "A.o"
        val runA = "run_A"
        val commandResponseA = "OK_A\n"

        val taskB = "B"
        val depsB = listOf("E")
        val targetNameB = "B.o"
        val runB = "run_B"
        val commandResponseB = "OK_B\n"

        val taskC = "C"
        val depsC = listOf("A", "B")
        val targetNameC = "C.o"
        val runC = "run_C"
        val commandResponseC = "OK_C\n"

        val fileParser = mock<FakeFileMapper> {
            on { mapParsedFileToTasks(any()) } doReturn mapOf(
                Pair(
                    taskA, Task(
                        taskName = taskA,
                        dependencyList = depsA,
                        targetFilename = targetNameA,
                        command = runA
                    )
                ),
                Pair(
                    taskB, Task(
                        taskName = taskB,
                        dependencyList = depsB,
                        targetFilename = targetNameB,
                        command = runB
                    )
                ),
                Pair(
                    taskC, Task(
                        taskName = taskC,
                        dependencyList = depsC,
                        targetFilename = targetNameC,
                        command = runC
                    )
                )
            )
        }

        var isCommandAExecuted = false

        val executor = mock<CommandExecutor> {
            on { executeCommand(runA) } doAnswer {
                isCommandAExecuted = true
                commandResponseA
            }
            on { executeCommand(runB) } doAnswer { commandResponseB }
            on { executeCommand(runC) } doAnswer { commandResponseC }
        }

        val dependencyChecker = mock<DependencyChecker> {
            on { isGreater(targetNameA, depsA.first()) } doAnswer { false }
            on { isGreater(targetNameB, depsB.first()) } doAnswer { true }

            on { isGreater(targetNameC, targetNameA) } doAnswer {
                !isCommandAExecuted
            }
            on { isGreater(targetNameC, targetNameB) } doAnswer { true }

            on { exists(depsA.first()) } doReturn true
            on { exists(depsB.first()) } doReturn true
            on { exists(depsC.first()) } doReturn true
            on { exists(targetNameA) } doReturn true
            on { exists(targetNameB) } doReturn true
            on { exists(targetNameC) } doReturn true
        }

        val r = FakeTaskRunner(mock(), executor, dependencyChecker, fileParser)

        val actual2 = r.run(taskC)
        val expected = "$runA\n$commandResponseA$runB\n${getUpToDate(taskB)}$runC\n$commandResponseC"
        assertEquals(expected, actual2)
    }
}