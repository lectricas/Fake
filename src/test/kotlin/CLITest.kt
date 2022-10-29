import domain.FakeFileRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import repository.UnixTestedCommandExecutor
import repository.YamlParser
import java.io.File


class CLITest {

    private val fileLocation = "fakefiles"

    @Test
    fun simpleTest() {
        val runner = FakeFileRunner(
            YamlParser("$fileLocation/cat_and_echo1.yaml"),
            UnixTestedCommandExecutor()
        )

        val actual = runner.run("task0")
        assertEquals("echo 0 > tsk0\n", actual)
        val file1 = File("tsk0")
        val actual1 = file1.readLines().first()
        val expected = "0"
        assertEquals(expected, actual1)
        file1.delete()
    }

    @Test
    fun multipleTest() {
        val runner = FakeFileRunner(
            YamlParser("$fileLocation/cat_and_echo2.yaml"),
            UnixTestedCommandExecutor()
        )

        val actual = runner.run("task1")
        val expected =
            """
                echo 0 > tsk0
                cat tsk0 > tsk1
                
                """.trimIndent()

        assertEquals(expected, actual)
        val file0 = File("tsk0")
        val file1 = File("tsk1")
        val actual1 = file1.readLines().first()
        val expected1 = "0"
        assertEquals(expected1, actual1)
        file0.delete()
        file1.delete()
    }
}