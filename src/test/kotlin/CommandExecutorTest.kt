import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import repository.UnixTestedCommandExecutor
import java.io.File

class CommandExecutorTest {
    @Test
    fun testSimple() {
        val executor = UnixTestedCommandExecutor()
        val actual = executor.executeCommand("echo hello")
        println(actual)
        assertEquals("hello\n", actual)
    }

    @Test
    fun testSimpleFile() {
        val filename = "file1"
        val executor = UnixTestedCommandExecutor()
        val actual = executor.executeCommand("echo hello > $filename")
        assertEquals("", actual)

        val file1 = File(filename)
        val actual1 = file1.readLines().first()
        val expected = "hello"
        assertEquals(expected, actual1)
        file1.delete()
    }

    @Test
    fun testCatFile() {
        val filename = "file0"
        val filename1 = "file1"
        val file0 = File(filename)
        file0.createNewFile()
        val executor = UnixTestedCommandExecutor()
        executor.executeCommand("cat $filename > $filename1")
        val file1 = File(filename1)
        assertTrue(file1.exists())
        file0.delete()
        file1.delete()
    }
}