package io.github.yusaka39.easySlackbot.test.router

import io.github.yusaka39.easySlackbot.router.AnnotationBasedHandlerSetFactory
import io.github.yusaka39.easySlackbot.router.Handler
import io.github.yusaka39.easySlackbot.router.HandlerType
import org.junit.Test
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertTrue

class AnnotationBasedHandlerSetFactoryTest {

    @Test
    fun handlerSetFactoryWorksFine() {
        val set = setOf(
            "foo" to HandlerType.ListenTo, "foo" to HandlerType.RespondTo,
            "bar" to HandlerType.ListenTo, "foobar" to HandlerType.RespondTo
        )

        val result = AnnotationBasedHandlerSetFactory(
                "io.github.yusaka39.easySlackbot.router.testHandlerPack"
        ).create().map {
            val regex = Handler::class.memberProperties.first { it.name == "regex" }.apply { this.isAccessible = true }
            val type =
                Handler::class.memberProperties.first { it.name == "handlerType" }.apply { this.isAccessible = true }
            regex.call(it).toString() to (type.call(it) as HandlerType)
        }
        assertTrue(result.containsAll(set) && set.containsAll(result))
    }
}

