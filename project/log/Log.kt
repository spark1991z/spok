package project.log

import java.text.SimpleDateFormat

/**
 * @author spark1991z
 */
class Log private constructor() {

    companion object {

        private var sdf: SimpleDateFormat = SimpleDateFormat("yyy-MM-dd@HH:mm:ss")
        private var INIT_TIMESTAMP: Long = System.currentTimeMillis()
        var DEBUG: Boolean = false

        private fun log(type: String, service: String, msg: String) {
            println("[$type] [$service] $msg")
        }

        fun info(service: String, msg: String) {
            log("INFO", service, msg)
        }

        fun debug(service: String, msg: String) {
            if (DEBUG)
                log("DEBUG", service, msg)
        }

        fun error(service: String, msg: String) {
            log("ERROR", service, msg)
        }
    }
}