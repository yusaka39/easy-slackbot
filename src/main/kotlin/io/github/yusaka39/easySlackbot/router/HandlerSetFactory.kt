package io.github.yusaka39.easySlackbot.router

interface HandlerSetFactory {
    fun create(): Set<Handler>
    companion object {
        fun default(): HandlerSetFactory = TODO()
    }
}

/*
    private val commands: List<Command> by lazy {
        ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive(searchPackage)
                .flatMap { clazz ->
                    clazz.load()?.kotlin?.let { kClass ->
                        kClass.members
                                .filter {
                                    it.isAnnotatedWith<ListenTo>() or it.isAnnotatedWith<RespondTo>()
                                }.map {
                                    val annotation = it.annotations.first { it is ListenTo || it is RespondTo }
                                    val type = ResponseType.from(annotation)!!
                                    val regex = type.getRegexFromAnnotation(annotation)
                                    Command(type, regex, kClass, it)
                                }
                    } ?: emptyList()
                }
    }
 */