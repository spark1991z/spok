package project.net.http

import project.log.Log
import java.net.Socket

class SocketListener(private var socket: Socket, private var listener: ServletListener?) : Runnable {

    companion object {
        private var LOG_SERVICE = javaClass.simpleName
    }


    @Synchronized override fun run() {
        Log.debug(javaClass.simpleName, "Connected $socket")
        var res: HttpResponse = HttpResponse(socket)
        var req: HttpRequest = HttpRequest(socket, res)
        if (!req.ready()) return
        if (listener != null) {
            listener!!.onServletRequire(req, res)
        } else res.println("It's work!")
        res.close()
    }


}