package unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import repository.ShellCommandExecutor
import java.io.File

class CommandExecutorTest {
    @Test
    fun test_echo_returned() {
        val executor = ShellCommandExecutor()
        val actual = executor.executeCommand("echo hello")
        assertEquals("hello\n", actual)
    }

    @Test
    fun test_file_created() {
        val filename = "file1"
        val executor = ShellCommandExecutor()
        val actual = executor.executeCommand("echo hello > $filename")
        assertEquals("", actual)

        val file = File(filename)
        val actualFile = file.readLines().first()
        val expected = "hello"
        assertEquals(expected, actualFile)

        file.delete()
    }

    @Test
    fun test_files_concatinated() {
        val filename = "file0"
        val filename1 = "file1"
        val file0 = File(filename)
        file0.createNewFile()
        val executor = ShellCommandExecutor()
        executor.executeCommand("cat $filename > $filename1")
        val file1 = File(filename1)
        assertTrue(file1.exists())

        file0.delete()
        file1.delete()
    }
}