package project.log

import java.text.SimpleDateFormat

/**
 * @author spark1991z
 */
class Log private constructor() {

    companion object {

        private var sdf: SimpleDateFormat = SimpleDateFormat("yyy-MM-dd@HH:mm:ss")
        private var init_timestamp: Long = System.currentTimeMillis()
        var DEBUG: Boolean = false

        private fun log(type: String, service: String, msg: String) {
            var t:String  = ((System.currentTimeMillis()- init_timestamp).toDouble()/1000).toString()
            if(t.split(".")[1].length<3) t+=0
            println("[$t][$type][$service] $msg")
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