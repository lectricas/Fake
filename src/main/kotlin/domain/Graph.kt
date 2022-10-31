package domain

class Graph<T>(private val graph: Map<T, List<T>>) {

    private val sorted: MutableList<T> = mutableListOf()
    private val visited: MutableMap<T, Boolean> = mutableMapOf()
    private val inStack: MutableMap<T, Boolean> = mutableMapOf()

    private fun dfs(from: T, parent: T?) {
        val adjacent = graph[from]
        if (adjacent != null) {
            visited[from] = true
            inStack[from] = true
            val fromNode = graph.getOrElse(from) {
                throw NodeNotFoundException(from.toString())
            }
            for (to in fromNode) {
                if (from !== parent &&
                    visited.getOrDefault(to, false) &&
                    inStack.getOrDefault(to, false)
                ) {
                    throw CycleException("$from to $to")
                }
                if (!visited.getOrDefault(to, false)) {
                    dfs(to, from)
                }
            }
            inStack[from] = false
            sorted.add(from)
        }
    }

    fun topSortFrom(taskName: T): List<T> {
        dfs(taskName, null)
        return sorted
    }
}

class CycleException(message: String) : Exception(message)
class NodeNotFoundException(message: String) : Exception(message)