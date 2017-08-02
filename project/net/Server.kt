package project.net

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import javax.net.ServerSocketFactory

/**
 * @author spark1991z
 */
abstract class Server(private var port: Int) : Runnable {

    private var server: ServerSocket? = null
    private var thread: Thread? = null
    private var runnable: Boolean = false

    fun start() {
        if (server != null && server!!.isBound && thread != null && runnable) return
        try {
            server = ServerSocketFactory.getDefault().createServerSocket(port)
            onServerStartedSuccessfully()
            runnable = true
            thread = Thread(this)
            thread!!.start()
        } catch (e: IOException) {
            onServerStartedFailed(e)
        }
    }

    fun stop() {
        if (server != null && server!!.isBound && thread != null && runnable) {
            try {
                runnable = false
                var s = Socket(InetAddress.getLocalHost(), port)
                s.close()
                server!!.close()
                server = null
                onServerStopedSuccessfully()
            } catch (e: IOException) {
                onServerStopedFailed(e)
            }
        }
    }

    fun restart() {
        stop()
        start()
    }

    fun runnable(): Boolean {
        return runnable
    }

    @Synchronized override fun run() {
        while (true) {
            try {
                var socket: Socket = server!!.accept()
                if (!runnable) {
                    socket.close()
                    break
                }
                if (!socket.isClosed()) {
                    onSocketConnected(socket)
                }
            } catch (e: IOException) {
                onUnableToAcceptIncommingConnection(e)
            }
        }
    }

    abstract fun onServerStartedSuccessfully()
    abstract fun onServerStopedSuccessfully()
    abstract fun onServerStartedFailed(e: IOException)
    abstract fun onServerStopedFailed(e: IOException)
    abstract fun onUnableToAcceptIncommingConnection(e: IOException)
    abstract fun onSocketConnected(socket: Socket)
}
