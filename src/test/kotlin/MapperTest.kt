import domain.YamlFakeFileMapper
import domain.FakeFileWrongFormat
import domain.Task
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MapperTest {

    val mapper = YamlFakeFileMapper()
    @Test
    fun testEmpty() {
        val actual = mapper.mapParsedFileToTasks(mapOf())
        val expected = mapOf<String, Task>()
        assertEquals(expected, actual)
    }

    @Test
    fun testSingle() {
        val parsed = mapOf(
            Pair(
                "compile", mapOf(
                    Pair("dependencies", listOf("task_00/main.c")),
                    Pair("target", "main.o"),
                    Pair("run", "gcc -c task_00/main.c -o main.o")
                )
            )
        )

        val actual = mapper.mapParsedFileToTasks(parsed)

        val expected = mapOf(
            Pair(
                "compile", Task(
                    taskName = "compile",
                    dependencyList = listOf("task_00/main.c"),
                    targetFilename = "main.o",
                    command = "gcc -c task_00/main.c -o main.o"
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun testMissingDependencies() {
        val parsed = mapOf(
            Pair(
                "compile", mapOf(
                    Pair("target", "main.o"),
                    Pair("run", "gcc -c task_00/main.c -o main.o")
                )
            )
        )

        val actual = mapper.mapParsedFileToTasks(parsed)

        val expected = mapOf(
            Pair(
                "compile", Task(
                    taskName = "compile",
                    listOf(),
                    targetFilename = "main.o",
                    command = "gcc -c task_00/main.c -o main.o"
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun testMissingTarget() {
        val parsed = mapOf(
            Pair(
                "compile", mapOf(
                    Pair("run", "gcc -c task_00/main.c -o main.o")
                )
            )
        )
        assertThrows<FakeFileWrongFormat> { mapper.mapParsedFileToTasks(parsed) }
    }

    @Test
    fun testMissingRun() {
        val parsed = mapOf(
            Pair(
                "compile", mapOf(
                    Pair("target", "main.o")
                )
            )
        )
        assertThrows<FakeFileWrongFormat> { mapper.mapParsedFileToTasks(parsed) }
    }
}