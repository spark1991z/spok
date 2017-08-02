package project.net.http

import project.crypto.MD5
import project.log.Loggable
import java.io.InputStream
import java.net.Socket
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*

/**
 * @author spark1991z
 */
class HttpRequest(private var socket: Socket, private var res: HttpResponse) : Loggable {

    companion object {

        private var methods: Vector<String> = Vector<String>()

        var METHOD_GET = "GET"
        var METHOD_POST = "POST"
        var METHOD_PUT = "PUT"
        var METHOD_HEAD = "HEAD"

        init {
            methods.add(METHOD_GET)
            methods.add(METHOD_POST)
            methods.add(METHOD_PUT)
            methods.add(METHOD_HEAD)
        }

        fun checkMethod(method: String?): Boolean {
            if (method != null)
                return methods.contains(method)
            return false
        }
    }

    private var method: String? = null
    private var path: String = "/"
    private var ready: Boolean = true


    private var headers: Hashtable<String, String> = Hashtable<String, String>()
    private var query: Hashtable<String, String> = Hashtable<String, String>()
    private var cookies:Hashtable<String,String> = Hashtable<String,String>()

    init {
        init()
    }

    private fun nextLine(): String? {
        var bf: String? = null
        while (true) {
            var r: Int = read()
            if (r < 0 || r.toChar() == '\n') break
            if (r.toChar() == '\r') continue
            if (bf == null) bf = ""
            bf += r.toChar()
        }
        return bf
    }


    private fun init() {
        /**
         * Base
         */
            var rs: String? = nextLine()

            if (rs == null) {
                ready = false
                socket.close()
                return
            }
            debug(rs)
            var h: List<String> = rs.split(" ")
        /**
         * Headers
         */
            while (true) {
                var line: String? = nextLine()
                if (line == null || line.isEmpty()) break
                debug(line)
                var y: Int = line.indexOf(":")
                var hn: String = line.substring(0, y).trim()
                var hv: String = line.substring(y + 1, line.length).trim()
                headers.put(hn, hv)
            }
        /**
         * Prepare
         */
            if (!methods.contains(h[0])) {
                ready = false
                res.status(HttpResponse.STATUS_501_NOT_IMPLEMENTED)
                res.printError("Method ${h[0]} is not exists or not supported on this server.")
                res.close()
                return
            }
            method = h[0]
            var x: Int = h[1].indexOf("%")
            if (x > 0) {
                try {
                    var uri: URI = URI(h[1])
                    path = uri.path
                    var x2:Int = h[1].indexOf("?")
                    if(x2>0){
                        for (kp in uri.query.split("&")) {
                            var kv: List<String> = kp.split("=")
                            debug("Query <- ${kv[0]}=${kv[1]}")
                            query.put(kv[0], kv[1])
                        }
                    }
                } catch (e: Exception) {
                    ready = false
                    res.status(HttpResponse.STATUS_400_BAD_REQUEST)
                    res.printError("Your request URI is bad: ${h[1]}")
                    res.close()
                    return
                }
            } else path = h[1]
            path = path!!.replace("///","/")
                         .replace("/../","/")
                         .replace("./","/")
                         .replace("//","/")
            if(!path!!.equals("/") && path!!.endsWith("/")) path = path!!.substring(0,path!!.length-1)
            var pv = h[2].split("/")
            var protocol: String = pv[0]
            var version: Double = java.lang.Double.valueOf(pv[1])
            if (!protocol.equals("HTTP") || version < HttpResponse.PROTOCOL_VERSION) {
                ready = false
                res.status(HttpResponse.STATUS_426_UPGRADE_REQUIRED)
                res.connection(HttpResponse.CONNECTION_UPGRADE)
                res.header("Upgrade", "HTTP/${HttpResponse.PROTOCOL_VERSION}")
                res.printError("This server requires use of the HTTP/${HttpResponse.PROTOCOL_VERSION} protocol.")
                res.close()
                return
            }
            //Cookies
            var v:String? = header("Cookie")
            if(v!=null){
                headers.remove("Cookie")
                for(cookie in v.split("; ")){
                    var kv:List<String> = cookie.split("=")
                    debug("Cookie <- ${kv[0]}=${kv[1]}")
                    cookies.put(kv[0],kv[1])
                }
            }
            //Cookie session_id
            var c:String? = cookie("session_id")
            if(c==null){
                var ua:String? = header("User-Agent")
                if(ua!=null)
                    res.cookie("session_id",MD5.digest(ua),null,60*60,null,"/",false,true)
            }
        /**
         * POST/PUT Query
         */
            if (method.equals(METHOD_POST) || method.equals(METHOD_PUT)) {
                var ctype: String? = header("Content-Type")
                if (ctype == null) {
                    ready = false
                    res.status(HttpResponse.STATUS_415_UNSUPPORTED_MEDIA_TYPE)
                    res.close()
                    return
                }
                var clen: Int = Integer.valueOf(header("Content-Length")).toInt()
                if (clen < 0) {
                    ready = false
                    res.status(HttpResponse.STATUS_411_LENGTH_REQUIRED)
                    res.close()
                    return
                }
                if (ctype.contains("boundary")) return //Disable accept files
                var qb: ByteArray = kotlin.ByteArray(clen)
                if(socket.isClosed()) return
                if (socket.getInputStream().read(qb) < clen) {
                    ready = false
                    res.status(HttpResponse.STATUS_422_UNPROCESSABLE_ENTITY)
                    res.close()
                    return
                }
                var q: String = String(qb)
                if (q.contains("%")) q = URLDecoder.decode(q,Charset.defaultCharset().displayName())
                debug(q)
                var query_arr: List<String> = q.split("&")
                for (kp in query_arr) {
                    var kv: List<String> = q.split("=")
                    debug("Query <- ${kv[0]}=${kv[1]}")
                    query.put(kv[0], kv[1])
                }
            }
    }

    fun cookie(key:String):String?{
        if(cookies.containsKey(key))
            return cookies.get(key)
        return null
    }

    fun read():Int{
        if(socket.isClosed()) return -1
        return socket.getInputStream().read()
    }

    fun ready(): Boolean {
        return ready
    }

    fun method(): String? {
        return method
    }

    fun path(): String {
        return path
    }

    fun header(key: String): String? {
        if (headers.containsKey(key))
            return headers.get(key)
        return null
    }

    fun query(key: String): String? {
        if (query.containsKey(key))
            return query.get(key)
        return null
    }
}