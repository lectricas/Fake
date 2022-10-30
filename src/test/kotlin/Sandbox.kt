import org.junit.jupiter.api.Test

class Sandbox {
    @Test
    fun test() {
        val allThey = (0..10).all {
            print(it)
            return@all it < 10
        }
        println(allThey)
    }
}