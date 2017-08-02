package project.net.http

import project.log.Loggable
import project.net.Server
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.Socket
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.*

class HttpServer(private var port: Int) : Server(port), Loggable, ServletListener {

    companion object {

        private var www_home: File = File("./www")
        private var www_html_home: File = File("${www_home}/html")
        private var servlets_src_home: File = File("${www_home}/servlets/src")
        private var servlets_bin_home: File = File("${www_home}/servlets/bin")

        init {
            www_home.mkdirs()
            www_html_home.mkdirs()
            servlets_src_home.mkdirs()
            servlets_bin_home.mkdirs()
        }
    }

    override fun onServerStartedSuccessfully() {
        info("Server successfuly started on port $port")
    }

    override fun onServerStopedSuccessfully() {
        info("Server successfully stoped")
    }

    override fun onServerStartedFailed(e: IOException) {
        error("Unable to start server:")
        e.printStackTrace()
    }

    override fun onServerStopedFailed(e: IOException) {
        error("Unable to stop server:")
        e.printStackTrace()
    }

    override fun onUnableToAcceptIncommingConnection(e: IOException) {
        error("Unable to accept incomming connection:")
        e.printStackTrace()
    }

    override fun onSocketConnected(socket: Socket) {
        var sl: Thread = Thread(SocketListener(socket, this))
        sl.start()
    }

    override fun onServletRequire(req: HttpRequest, res: HttpResponse) {
        info("[${req.method()}] ${req.path()}")
        var fl: File = File("$www_html_home/${req.path()}")
        if (!fl.exists()) {
            res.status(HttpResponse.STATUS_404_NOT_FOUND)
            res.printError("Object ${req.path()} not found on this server.")
            res.close()
            return
        }
        if (fl.isDirectory()) {
            var fl2:File = File("$fl/index.htm")
            if (fl2.isFile()) {
                res.status(HttpResponse.STATUS_303_SEE_OTHER)
                res.header("Location", "index.htm")
                res.close()
                return
            }
            fl2 = File("$fl/index.html")
            if (fl2.isFile()) {
                res.status(HttpResponse.STATUS_303_SEE_OTHER)
                res.header("Location", "index.html")
                res.close()
                return
            }
            res.status(HttpResponse.STATUS_403_FORBIDDEN)
            res.close()
            return
        }
        var fin: FileInputStream = FileInputStream(fl)
        res.contentType(Files.probeContentType(fl.toPath()))
        res.header("Content-Length",fl.length().toString())
        var bf: ByteArray = kotlin.ByteArray(512)
        while (true) {
            var r: Int = fin.read(bf)
            if (r < 0) break
            res.write(bf, 0, r)
        }
        fin.close()
        res.close()
    }
}