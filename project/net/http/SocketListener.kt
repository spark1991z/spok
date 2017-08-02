package project.net.http

import project.log.Log
import project.log.Loggable
import java.net.Socket

/**
 * @author spark1991z
 */
class SocketListener(private var socket: Socket, private var listener: ServletListener?) : Runnable,Loggable {

    @Synchronized override fun run() {

        debug( "Connected $socket")
        var res: HttpResponse = HttpResponse(socket)
        var req: HttpRequest = HttpRequest(socket, res)
        if (!req.ready()) return
        if (listener != null) {
            listener!!.onServletRequire(req, res)
        } else res.println("It's work!")
        res.close()
    }

}