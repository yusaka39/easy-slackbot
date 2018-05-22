package io.github.yusaka39.easySlackbot.scheduler

import com.google.common.reflect.ClassPath
import io.github.yusaka39.easySlackbot.annotations.RunWithInterval
import io.github.yusaka39.easySlackbot.lib.logger
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

internal class AnnotationBasedScheduledTaskSetFactory(private val packageName: String) : ScheduledTaskSetFactory {
    private val logger by this.logger()
    override fun create(): Set<ScheduledTask> =
            ClassPath.from(ClassLoader.getSystemClassLoader()).allClasses.filter {
                it.packageName.startsWith(packageName)
            }.flatMap {
                try {
                    val kClass = it.load().kotlin
                    (kClass.members + kClass.memberProperties.map { it.getter }).mapNotNull {
                        val annotation = it.findAnnotation<RunWithInterval>()
                                ?: return@mapNotNull null
                        ScheduledTask(kClass, it, Schedule(annotation))
                    }
                } catch (e: UnsupportedOperationException) {
                    this.logger.warn("Failed to parse class ${it.name}.", e)
                    emptyList<ScheduledTask>()
                }
            }.toSet()
}