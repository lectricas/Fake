import domain.CycleException
import domain.Graph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GraphTest {

    @Test
    fun testOnce() {
        val map = mapOf(Pair(0, listOf<Int>()))
        val graph = Graph(map)
        val actual = graph.topSortFrom(0)
        val expected = listOf(0)
        assertEquals(expected, actual)
    }

    @Test
    fun testOnceString() {
        val map = mapOf(Pair("task0", listOf<String>()))
        val graph = Graph(map)
        val actual = graph.topSortFrom("task0")
        val expected = listOf("task0")
        assertEquals(expected, actual)
    }

    @Test
    fun testSimple() {
        val map = mapOf(
            Pair(0, listOf(1, 2)),
            Pair(1, listOf(2))
        )
        val graph = Graph(map)
        val actual = graph.topSortFrom(0)
        val expected = listOf(1, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun testBamboo() {
        val map = mapOf(
            Pair(0, listOf(1)),
            Pair(1, listOf(2)),
            Pair(2, listOf(3)),
            Pair(3, listOf(4)),
            Pair(4, listOf(5)),
            Pair(5, listOf(6)),
            Pair(6, listOf(7)),
            Pair(7, listOf(8)),
            Pair(8, listOf(9)),
            Pair(9, listOf(10)),
        )
        val graph = Graph(map)
        val actual = graph.topSortFrom(0)
        val expected = listOf(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun testSimpleException() {
        val map = mapOf(
            Pair(0, listOf(1)),
            Pair(1, listOf(2)),
            Pair(2, listOf(3)),
            Pair(3, listOf(1)),
        )
        val graph = Graph(map)
        assertThrows<CycleException> {
            println(graph.topSortFrom(0))
        }
    }
}