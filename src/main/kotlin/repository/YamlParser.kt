package repository

import domain.FakeFileNotFound
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.scanner.ScannerException
import java.io.File
import java.io.FileInputStream

class YamlParser(private val defaultFakeFile: String = "fakefile.yaml") : FakeFileParser {

    private val yaml = Yaml()

    override fun parseDefaultFakeFile(): Map<String, Any> {
        return parseYAML(defaultFakeFile)
    }

    fun parseYAML(filename: String): Map<String, Any> {
        val fakeFile = getFakeFile(filename)
        return parseYAMLFile(fakeFile)
    }

    private fun getFakeFile(filename: String): File {
        val fakeFile = File(filename)
        if (!fakeFile.exists()) {
            throw FakeFileNotFound(filename)
        }
        return fakeFile
    }

    private fun parseYAMLFile(fakefile: File): Map<String, Any> {
        val inputStream = FileInputStream(fakefile)
        return yaml.load(inputStream) ?: mapOf()
    }
}