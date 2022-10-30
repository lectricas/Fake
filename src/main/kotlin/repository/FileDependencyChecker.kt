package repository

import java.nio.file.Files
import java.nio.file.Paths

class FileDependencyChecker : DependencyChecker {
    override fun exists(dependency: String): Boolean {
        val path = Paths.get(dependency)
        return Files.exists(path)
    }

    override fun compareTime(target: String, dependency: String): Int {
        val dependencyFile = Paths.get(dependency)
        val dependencyTime = Files.getLastModifiedTime(dependencyFile)
        val targetFile = Paths.get(target)
        val targetTime = Files.getLastModifiedTime(targetFile)
        return targetTime.compareTo(dependencyTime)
    }
}