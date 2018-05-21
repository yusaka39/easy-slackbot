package io.github.yusaka39.easySlackbot.api.handler

import io.github.yusaka39.easySlackbot.api.entity.Action
import io.github.yusaka39.easySlackbot.api.entity.Message
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

abstract class HandlerPackHandler : Handler {
    protected abstract val kCallable: KCallable<*>
    protected abstract val kClass: KClass<*>

    override fun createActionProvider(message: Message): () -> Action = {
        this.kCallable.apply {
            this.isAccessible = true
        }.call(
                this.kClass.instantiateHandlerPack(message),
                *this.getArgumentsFromTargetMessage(message)
        ) as? Action ?: throw IllegalStateException("Handler functions must return Action")
    }

    protected abstract fun getArgumentsFromTargetMessage(message: Message): Array<Any?>

    private fun KClass<*>.instantiateHandlerPack(message: Message? = null): Any {
        if (!this.isSubclassOf(HandlerPack::class)) {
            throw IllegalStateException("Classes contains handler must extends HandlerPack")
        }
        return this.constructors.firstOrNull { it.parameters.isEmpty() }?.apply {
            this.isAccessible = true
        }?.call()?.apply {
            (this as HandlerPack).setReceivedMessage(message)
        } ?: throw IllegalStateException(
                "Classes contains handler function must have constructor with no args"
        )
    }
}