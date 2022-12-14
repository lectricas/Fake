package unit

import domain.FakeFileNotFound
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.yaml.snakeyaml.scanner.ScannerException
import repository.YamlParser

class YamlParserTest {

    private val parser = YamlParser()

    private val fileLocation = "fakefiles"

    @Test
    fun read_empty_ok() {
        val actual = parser.parseYAML("$fileLocation/empty.yaml")
        assertEquals(emptyMap<String, Any>(), actual)
    }

    @Test

    fun read_broken_format_file_error() {
        assertThrows<ScannerException> { parser.parseYAML("$fileLocation/corrupt.yaml") }
    }

    @Test
    fun read_missing_file_error() {
        assertThrows<FakeFileNotFound> { parser.parseYAML("$fileLocation/missing.yaml") }
    }

    @Test
    fun read_single_target() {
        val actual = parser.parseYAML("$fileLocation/1.yaml")
        val expected = mapOf(
            Pair(
                "compile", mapOf(
                    Pair("dependencies", listOf("task_00/main.c")),
                    Pair("target", "main.o"),
                    Pair("run", "gcc -c task_00/main.c -o main.o")
                )
            )
        )
        assertEquals(expected, actual)
    }
}