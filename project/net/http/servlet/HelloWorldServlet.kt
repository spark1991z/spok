package project.net.http.servlet

import project.net.http.HttpRequest
import project.net.http.HttpResponse
import project.net.http.HttpServlet

class HelloWorldServlet() : HttpServlet("hw") {

    override fun doPost(req: HttpRequest, res: HttpResponse, servletPath: String) {
        doGetOrPost(req, res, servletPath)
    }

    override fun doPut(req: HttpRequest, res: HttpResponse, servletPath: String) {
    }

    override fun doHead(req: HttpRequest, res: HttpResponse, servletPath: String) {
    }

    override fun doGet(req: HttpRequest, res: HttpResponse, servletPath: String) {
        doGetOrPost(req, res, servletPath)
    }

    private fun doGetOrPost(req: HttpRequest, res: HttpResponse, servletPath: String) {
        res.println("Hello world!")
        res.close()
    }
}