package repository

interface FakeFileParser {
    fun parseDefaultFakeFile(): Map<String, Any>
}