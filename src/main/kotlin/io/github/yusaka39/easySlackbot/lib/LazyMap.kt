package io.github.yusaka39.easySlackbot.lib

internal class LazyMap<K, V> private constructor(
    private val cache: MutableMap<K, V>,
    private val getter: (key: K) -> V?
) : Map<K, V> by cache {
    constructor(cacheInitializer: () -> Map<K, V>, getter: (key: K) -> V?) :
            this(cacheInitializer().toMutableMap(), getter)

    constructor(getter: (key: K) -> V?) : this(mutableMapOf(), getter)

    override operator fun get(key: K): V? {
        this.cache[key]?.let { return it }
        return this.getter(key)?.let {
            this.cache[key] = it
            it
        }
    }

    override fun getOrDefault(key: K, defaultValue: V): V = this[key] ?: defaultValue
}

internal fun <K, V> memoize(getter: (K) -> V) = LazyMap(getter)
