package project.net.http

interface ServletListener {

    fun onServletRequire(req: HttpRequest, res: HttpResponse)
}