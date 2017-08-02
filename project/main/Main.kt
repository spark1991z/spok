package project.main

import project.log.Log
import project.log.Loggable
import project.net.Server
import project.net.http.HttpServer

/**
 * @author spark1991z
 */
class Main private constructor() : Loggable {

    companion object {

        private var main: Main? = null
        private var RZD: String = "------------------------------"
        var PROJECT_NAME: String = "Spok"
        var VERSION_CORE: Double = 0.8
        var VERSION_CODE: Int = 2
        var VERSION_BUILD: Double = 203.9
        var SYSTEM_OS_NAME: String = System.getProperty("os.name")
        var SYSTEM_OS_ARCH: String = System.getProperty("os.arch")

        @JvmStatic fun instance(): Main? {
            return main
        }

        @JvmStatic fun main(args: Array<String>) {
            if (main != null) return
            main = Main()
            println("${System.currentTimeMillis()}\n$RZD\n$main\n$RZD")
            Log.DEBUG = false
            main!!.start()
        }
    }

    private var ws: Server? = null

    override fun toString(): String {
        return "$PROJECT_NAME $VERSION_CORE.$VERSION_CODE-$VERSION_BUILD ($SYSTEM_OS_NAME, $SYSTEM_OS_ARCH)"
    }

    fun start() {
        info("Starting services...")
        if (ws == null) {
            ws = HttpServer(9999);
            ws!!.start()
        }
    }

    fun stop() {
        info("Stoping services...")
        if (ws!!.runnable()) {
            ws!!.stop();
            ws = null;
        }
    }

    fun restart() {
        stop()
        start()
    }

    fun shutdown() {
        stop()
        info("Program will be shutdowned.\n" +
                "Bye.")
        System.exit(0)
    }
}

