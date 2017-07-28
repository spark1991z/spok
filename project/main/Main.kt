package project.main

import project.log.Log;
/**
 * @author spark1991z
 */
class Main private constructor() {

    override fun toString(): String {
        return "$PROJECT_NAME $VERSION_CORE.$VERSION_CODE-$VERSION_BUILD ($SYSTEM_OS_NAME, $SYSTEM_OS_ARCH)"
    }

    companion object {

        private var main: Main? = null

        private var PROJECT_NAME: String? = null
        private var VERSION_CORE: Double? = null
        private var VERSION_CODE: Int? = null
        private var VERSION_BUILD: Double? = null
        private var RZD = "------------------------------"
        private var SYSTEM_OS_NAME: String? = null
        private var SYSTEM_OS_ARCH: String? = null

        init {
            PROJECT_NAME = "Spok"
            VERSION_CORE = 0.1
            VERSION_CODE = 7
            VERSION_BUILD = 29.1
            SYSTEM_OS_NAME = System.getProperty("os.name")
            SYSTEM_OS_ARCH = System.getProperty("os.arch")
        }

        @JvmStatic fun main(args: Array<String>) {
            if (main != null) return
            main = Main()
            println("${System.currentTimeMillis()}\n$RZD\n$main\n$RZD")
            start()
        }

        fun start() {
            Log.info("Starting services...")
        }

        fun stop() {
            Log.info("Stoping services...")
        }

        fun restart() {
            Log.info("Restarting...")
            stop()
            start()
        }

        fun shutdown() {
            stop()
            Log.info("Program will be shutdowned.\n" +
                    "Bye.")
            System.exit(0)
        }
    }
}

