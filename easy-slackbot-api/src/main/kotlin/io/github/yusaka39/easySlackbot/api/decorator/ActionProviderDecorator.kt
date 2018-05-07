package io.github.yusaka39.easySlackbot.api.decorator

import io.github.yusaka39.easySlackbot.api.entity.Action

interface ActionProviderDecorator {
    fun decorate(provider: () -> Action): () -> Action

    infix fun compose(decorator: ActionProviderDecorator): ActionProviderDecorator {
        return object : ActionProviderDecorator {
            override fun decorate(provider: () -> Action): () -> Action =
                    this.decorate(decorator.decorate(provider))
        }
    }
}