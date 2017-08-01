package project.net.http

import java.util.*

abstract class HttpServlet {

    private var methods: Vector<String> = Vector<String>()

    constructor(methods: Array<String>) {

        if (methods.size >= 1) {
            var c: Int = 0
            while (c < methods.size) {
                if (!HttpRequest.checkMethod(methods[c]))
                    throw IllegalArgumentException("Unknown method: ${methods[c]}")
                if (this.methods.contains(methods[c]))
                    throw IllegalArgumentException("Duplicate method: ${methods[c]}")
                this.methods.add(methods[c])
                c++
            }
        }
    }

    protected fun service(req: HttpRequest, res: HttpResponse) {
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
        when (req.method()) {
            HttpRequest.METHOD_GET -> doGet(req, res)
            HttpRequest.METHOD_POST -> doPost(req, res)
            HttpRequest.METHOD_PUT -> doPut(req, res)
            HttpRequest.METHOD_HEAD -> doHead(req, res)
        }
    }

    abstract fun doGet(req: HttpRequest, res: HttpResponse)
    abstract fun doPost(req: HttpRequest, res: HttpResponse)
    abstract fun doPut(req: HttpRequest, res: HttpResponse)
    abstract fun doHead(req: HttpRequest, res: HttpResponse)
}