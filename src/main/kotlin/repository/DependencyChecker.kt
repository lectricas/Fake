package repository

interface DependencyChecker {
    fun exists(dependency: String): Boolean

    fun compareTime(target: String, dependency: String): Int
}