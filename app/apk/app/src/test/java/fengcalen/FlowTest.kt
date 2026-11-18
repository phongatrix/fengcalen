package fengcalen

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowTest {
    @Test
    fun testFlowEmission() = runBlocking {
        println("Testing safe flow emission without withContext...")
        val f = flow {
            emit("test-safe-flow")
        }.flowOn(Dispatchers.IO)

        var result = ""
        f.collect {
            result = it
            println("Collected: $it")
        }
        assertEquals("test-safe-flow", result)
    }
}
