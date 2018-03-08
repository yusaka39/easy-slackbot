package io.github.yusaka39.easySlackbot.scheduler

import io.github.yusaka39.easySlackbot.scheduler.testTaskHolder.TestTaskHolder
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test
import kotlin.test.assertTrue


class AnnotationBasedScheduledTaskSetFactoryTest {

    private inline fun <reified T: Annotation> KAnnotatedElement.findAnnotationNotNull(): T =
        this.findAnnotation() ?: throw AssertionError()

    private fun ScheduledTask.isSameTo(task: ScheduledTask): Boolean {
        val kClass = ScheduledTask::class.memberProperties.first { it.name == "kClass" }.getter.apply {
            this.isAccessible = true
        }
        val kCallable = ScheduledTask::class.memberProperties.first { it.name == "kCallable" }.getter.apply {
            this.isAccessible = true
        }
        val zoneId = Schedule::class.memberProperties.first { it.name == "zoneId" }.getter.apply {
            this.isAccessible = true
        }
        return kClass(this) == kClass(task)
                && kCallable(this) == kCallable(task)
                && this.schedule.startHour == task.schedule.startHour
                && this.schedule.startMin == task.schedule.startMin
                && this.schedule.intervalMillis == task.schedule.intervalMillis
                && zoneId(this.schedule) == zoneId(task.schedule)
    }


    @Test
    fun factoryReturnsExpectedSet() {
        val expected = setOf(
            ScheduledTask(
                TestTaskHolder::class, TestTaskHolder::foo, Schedule(TestTaskHolder::foo.findAnnotationNotNull())
            ),
            ScheduledTask(
                TestTaskHolder::class, TestTaskHolder::bar, Schedule(TestTaskHolder::bar.findAnnotationNotNull())
            ),
            ScheduledTask(
                TestTaskHolder::class, TestTaskHolder::baz, Schedule(TestTaskHolder::baz.findAnnotationNotNull())
            )
        )
        val actual = AnnotationBasedScheduledTaskSetFactory(
            "io.github.yusaka39.easySlackbot.scheduler.testTaskHolder"
        ).create()
        assertTrue(actual.all { actual -> expected.any { it.isSameTo(actual) } })
        assertTrue(expected.all { expected -> actual.any { it.isSameTo(expected) } })
    }
}