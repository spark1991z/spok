package project.log

/**
 * @author spark1991z
 */
interface Loggable {

    fun info(msg: String) {
        Log.info(javaClass.simpleName, msg)
    }

    fun debug(msg: String) {
        Log.debug(javaClass.simpleName, msg)
    }

    fun error(msg: String) {
        Log.error(javaClass.simpleName, msg)
    }
}