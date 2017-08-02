package project.net.http

import java.util.*

abstract class HttpServlet(protected var name: String) {

    private var methods: Vector<String> = Vector<String>()

    init {
        add("GET")
        add("POST")
    }

    fun add(method: String) {
        if (!HttpRequest.checkMethod(method))
            throw IllegalArgumentException("Unknown method: ${method}")
        if (this.methods.contains(method))
            throw IllegalArgumentException("Duplicate method: ${method}")
        methods.add(method)
    }

    fun service(req: HttpRequest, res: HttpResponse) {
        if (methods.size < 1) {
            res.status(HttpResponse.STATUS_501_NOT_IMPLEMENTED)
            res.close()
            return
        }
        if (!methods.contains(req.method())) {
            res.status(HttpResponse.StATUS_405_METHOD_NOT_ALLOWED)
            res.printError("This servlet is not allowed method '${req.method()}.\nSupported methods: $methods")
            var allow: String = ""
            var m: Enumeration<String> = methods.elements()
            var first: Boolean = true
            while (m.hasMoreElements()) {
                if (!first) allow += ";"
                else first = false
                allow += m.nextElement()
            }
            res.header("Allow", allow)
            res.close()
            return
        }
        var path: String = req.path().replace("/servlet/$name", "")
        if (path.isEmpty()) path = "/"
        when (req.method()) {
            HttpRequest.METHOD_GET -> doGet(req, res, path)
            HttpRequest.METHOD_POST -> doPost(req, res, path)
            HttpRequest.METHOD_PUT -> doPut(req, res, path)
            HttpRequest.METHOD_HEAD -> doHead(req, res, path)
        }
    }

    fun name(): String {
        return name
    }

    fun redirect(path: String, res: HttpResponse) {
        res.redirect("/servlet/$name/$path")
    }

    abstract fun doGet(req: HttpRequest, res: HttpResponse, servletPath: String)
    abstract fun doPost(req: HttpRequest, res: HttpResponse, servletPath: String)
    abstract fun doPut(req: HttpRequest, res: HttpResponse, servletPath: String)
    abstract fun doHead(req: HttpRequest, res: HttpResponse, servletPath: String)
}