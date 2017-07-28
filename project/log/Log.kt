package project.log
/**
 * @author spark1991z
 */
class Log private constructor() {

    companion object {

        private var INIT_TIMESTAMP: Long? = null
        private var DEBUG: Boolean

        init {
            INIT_TIMESTAMP = System.currentTimeMillis()
            DEBUG = false
        }

        private fun log(type: String?=null, msg: String?=null) {
            println("[$type] $msg")
        }

        fun isDebug(debug:Boolean){
            DEBUG=debug
        }

        fun isDebug():Boolean{
            return DEBUG
        }

        fun info(msg: String) {
            log("INFO", msg)
        }

        fun error(msg: String) {
            log("ERROR", msg)
        }

        fun debug(msg: String) {
            if (DEBUG) log("DEBUG", msg)
        }
    }
}