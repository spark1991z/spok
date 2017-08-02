package project.net.http

import project.log.Loggable
import project.net.Server
import project.net.http.servlet.HelloWorldServlet
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.Socket
import java.nio.file.Files
import java.util.*

class HttpServer(private var port: Int) : Server(port), Loggable, ServletListener {

    companion object {

        private var www_home: File = File("./www")
        private var www_html_home: File = File("${www_home}/html")
        private var servlets_src_home: File = File("${www_home}/servlets/src")
        private var servlets_bin_home: File = File("${www_home}/servlets/bin")
        private var servlets: Hashtable<String, HttpServlet> = Hashtable<String, HttpServlet>()

        init {
            www_home.mkdirs()
            www_html_home.mkdirs()
            servlets_src_home.mkdirs()
            servlets_bin_home.mkdirs()
            register(HelloWorldServlet())
        }

        fun register(servlet: HttpServlet) {
            if (!servlets.containsKey(servlet.name()))
                servlets.put(servlet.name(), servlet)
            else throw IllegalAccessException("HttpServlet ${servlet.name()} already registered")
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
        var sp: List<String> = req.path().split("/")

        if (sp.size >= 2 && sp[1].equals("servlet")) {
            var sn: String? = null
            if (sp.size >= 3) {
                sn = sp[2]
                var hs: HttpServlet? = servlets.get(sn)
                if (hs != null) {
                    hs.service(req, res)
                    return
                }
                res.redirect("/servlet")
                return
            }
            var list: String = ""
            if (servlets.size > 0) {
                for (name in servlets.keys()) {
                    list += "<a href=/servlet/$name>$name</a><br>"
                }
            } else list = "Not found installed servlets"
            res.contentType("text/html")
            res.println("<html><head><title>Servlets</title></head><body><h2>Servlet</h2>$list</body></html>")
            res.close()
            return
        }

        var fl: File = File("$www_html_home/${req.path()}")
        if (!fl.exists()) {
            res.status(HttpResponse.STATUS_404_NOT_FOUND)
            res.printError("Object ${req.path()} not found on this server.")
            res.close()
            return
        }
        if (fl.isDirectory()) {
            var fl2: File = File("$fl/index.htm")
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
        res.header("Content-Length", fl.length().toString())
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