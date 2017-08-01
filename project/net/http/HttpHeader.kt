package project.net.http

import java.util.*

class HttpHeader {

    companion object {

        public var MODE_VALUES = 0
        public var MODE_PARAMS = 1

        private var modes: Vector<Int> = Vector<Int>()
        private var ERR_HASMORE_ENABLED = "HasMore is enabled. Usage:"
        private var ERR_HASMORE_DISABLED = "HasMore is disabled. Usage:"
        private var ERR_DUPLICATE = "Value already exists:"
        private var ERR_MODE = "Invalid mode:"
        private var ERR_MODES = "This function must be used with mode:"

        init {
            modes.add(MODE_VALUES)
            modes.add(MODE_PARAMS)
        }
    }

    constructor(hasMore: Boolean) {
        mode = MODE_VALUES
        this.hasMore = hasMore
    }

    constructor() {}

    private var mode: Int = MODE_PARAMS
    private var hasMore: Boolean = false
    private var values: Vector<String> = Vector<String>()
    private var params: Hashtable<String, String> = Hashtable<String, String>()

    init {
        if (!modes.contains(mode))
            throw IllegalArgumentException("$ERR_MODE $mode")
    }


    fun values(): Enumeration<String> {
        if (mode == MODE_VALUES) {
            if (hasMore)
                return values.elements()
            else throw IllegalAccessException("$ERR_HASMORE_DISABLED value()")
        } else return params.keys()
    }

    fun value(): String? {
        if (mode == MODE_VALUES) {
            if (!hasMore) {
                if (values.size > 0)
                    return values.get(0)
            } else throw IllegalAccessException("$ERR_HASMORE_ENABLED values()")
        } else throw IllegalAccessException("$ERR_MODES values")
        return null
    }

    fun value(key: String): String? {
        if (mode == MODE_PARAMS) {
            if (params.containsKey(key))
                return params.get(key)
        } else throw IllegalAccessException("$ERR_MODES params")
        return null
    }

    fun add(value: String) {
        if (mode == MODE_VALUES) {
            if (hasMore) {
                if (!values.contains(value))
                    values.add(value)
                else throw IllegalAccessException("$ERR_DUPLICATE $value")
            } else throw IllegalAccessException("$ERR_HASMORE_DISABLED set($value)")
        } else throw IllegalAccessException("$ERR_MODES values")
    }

    fun set(key: String, value: String) {
        if (mode == MODE_PARAMS) {
            params.put(key, value)
        } else throw IllegalAccessException("$ERR_MODES params")
    }

    fun set(value: String) {
        if (mode == MODE_VALUES) {
            if (!hasMore) {
                if (values.size > 0)
                    values.set(0, value)
                else values.add(value)
            } else throw IllegalAccessException("$ERR_HASMORE_ENABLED add($value)")
        } else throw IllegalAccessException("$ERR_MODES values")
    }

    fun hasMore(): Boolean {
        return hasMore
    }

    fun mode(): Int {
        return mode;
    }
}