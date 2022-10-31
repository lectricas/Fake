package repository

interface DependencyChecker {
    fun exists(dependency: String): Boolean

    fun isGreater(target: String, dependency: String): Boolean
}