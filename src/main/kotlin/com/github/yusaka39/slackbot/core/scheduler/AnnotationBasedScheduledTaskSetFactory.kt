package com.github.yusaka39.slackbot.core.scheduler

import com.google.common.reflect.ClassPath
import com.github.yusaka39.slackbot.core.annotations.RunWithInterval
import com.github.yusaka39.slackbot.core.lib.logger
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