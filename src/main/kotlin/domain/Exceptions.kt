package domain

open class FakeException(message: String) : Exception(message)

class RuleNotFound(message: String) : FakeException(message)
class FakeFileNotFound(message: String) : FakeException(message)
class FakeFileWrongFormat(message: String) : FakeException(message)


