package project.net.http

import project.log.Loggable
import project.main.Main
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

/**
 * @author spark1991z
 */
class HttpResponse(private var socket: Socket) : Loggable {

    companion object {
        //Connections
        var CONNECTION_CONTINUE: Int = 0
        var CONNECTION_CLOSE: Int = 1
        var CONNECTION_UPGRADE: Int = 2
        var CONNECTION_KEEP_ALIVE: Int = 3

        var PROTOCOL_VERSION: Double = 1.1

        /**
         * Base
         */
        //Informational
        var STATUS_100_CONTINUE: Int = 100
        //Success
        var STATUS_200_OK: Int = 200
        var STATUS_204_NO_CONTENT: Int = 204
        //Redirection
        var STATUS_303_SEE_OTHER: Int = 303
        //Client error
        var STATUS_400_BAD_REQUEST: Int = 400
        var STATUS_401_UNAUTHORIZED: Int = 401
        var STATUS_403_FORBIDDEN: Int = 403
        var STATUS_404_NOT_FOUND: Int = 404
        var StATUS_405_METHOD_NOT_ALLOWED: Int = 405
        var STATUS_411_LENGTH_REQUIRED: Int = 411
        var STATUS_415_UNSUPPORTED_MEDIA_TYPE: Int = 415
        var STATUS_422_UNPROCESSABLE_ENTITY: Int = 422
        var STATUS_426_UPGRADE_REQUIRED: Int = 426
        //Server error
        var STATUS_500_INTERNAL_SERVER_ERROR: Int = 500
        var STATUS_501_NOT_IMPLEMENTED: Int = 503

        private var status_msg: Hashtable<Int, String> = Hashtable<Int, String>()
        private var connection_names: Hashtable<Int, String> = Hashtable<Int, String>()

        init {
            connection_names.put(CONNECTION_CONTINUE, "Continue")
            connection_names.put(CONNECTION_CLOSE, "Close")
            connection_names.put(CONNECTION_UPGRADE, "Upgrade")
            connection_names.put(CONNECTION_KEEP_ALIVE,"Keep-Alive")

            status_msg.put(STATUS_200_OK, "OK")
            status_msg.put(STATUS_204_NO_CONTENT, "No Content")
            status_msg.put(STATUS_303_SEE_OTHER, "See Other")
            status_msg.put(STATUS_400_BAD_REQUEST, "Bad Request")
            status_msg.put(STATUS_401_UNAUTHORIZED, "Unauthorized")
            status_msg.put(STATUS_403_FORBIDDEN, "Forbidden")
            status_msg.put(STATUS_404_NOT_FOUND, "Not Found")
            status_msg.put(StATUS_405_METHOD_NOT_ALLOWED, "Method Not Allowed")
            status_msg.put(STATUS_411_LENGTH_REQUIRED, "Length Required")
            status_msg.put(STATUS_415_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type")
            status_msg.put(STATUS_422_UNPROCESSABLE_ENTITY, "Unprocessable Entity")
            status_msg.put(STATUS_426_UPGRADE_REQUIRED, "Upgrade Required")
            status_msg.put(STATUS_500_INTERNAL_SERVER_ERROR, "Internal Server Error")
            status_msg.put(STATUS_501_NOT_IMPLEMENTED, "Not Implemented")
        }
    }

    private var status: Int = STATUS_200_OK
    private var headers: Hashtable<String, HttpHeader> = Hashtable<String, HttpHeader>()
    private var sendHeaders: Boolean = false
    private var closed: Boolean = false


    init {
        connection(CONNECTION_KEEP_ALIVE)
        contentType("text/plain")
    }

    fun status(status: Int) {
        if (status_msg.containsKey(status))
            this.status = status;
        else throw IllegalArgumentException("Status '$status' is not exists or not supported on server!")
    }

    fun isSet(name: String): Boolean {
        return headers.containsKey(name)
    }

    fun header(name: String, key: String, value: String) {
        var header: HttpHeader? = headers.get(name)
        if (header == null)
            header = HttpHeader()
        header.set(key, value)
        headers.put(name, header)
    }

    fun header(name: String, value: String) {
        header(name, value, false)
    }

    fun header(name: String, value: String, hasMore: Boolean) {
        var header: HttpHeader? = headers.get(name)
        if (header != null) {
            if (header.hasMore()) {
                header.add(value)
            } else header.set(value)
        } else {
            header = HttpHeader(hasMore)
            if (hasMore)
                header.add(value)
            else header.set(value)
        }
        headers.put(name, header)
    }


    fun connection(connection: Int) {
        if (connection_names.containsKey(connection))
            header("Connection", "${connection_names.get(connection)}", false)
        else throw IllegalArgumentException("Connection type '$connection' is not exists or not supported on server!")
    }

    fun contentType(type: String) {
        contentType(type, Charset.defaultCharset().displayName())
    }

    fun contentType(type: String, charset: String?) {
        if (charset != null)
            header("Content-Type", "$type; charset=$charset")
        else
            header("Content-Type", type)
    }

    private fun sendHeaders() {
        if (!sendHeaders) {
            debug("HTTP/$PROTOCOL_VERSION $status ${status_msg.get(status)}")
            if(socket.isClosed()) return
            socket.getOutputStream().write("HTTP/1.1 $status ${status_msg.get(status)}\r\n".toByteArray())
            for(key in headers.keys()){
                var header:HttpHeader? = headers.get(key)
                if(header!!.mode()==HttpHeader.MODE_VALUES){
                    if(header.hasMore()){
                        for(value in header.values()){
                            debug("$key: $value")
                            if(socket.isClosed()) return
                            socket.getOutputStream().write("$key: $value\r\n".toByteArray())
                        }
                        continue
                    }
                    debug("$key: ${header.value()}")
                    if(socket.isClosed()) return
                    socket.getOutputStream().write("$key: ${header.value()}\r\n".toByteArray())
                    continue
                }
                var value:String = ""
                var first:Boolean = true
                for(k in header.values()){
                    if(!first) value+=";"
                    else first = false
                    value += "$k=${header.value(k)}"
                }
                debug("$key: $value")
                if(socket.isClosed()) return
                socket.getOutputStream().write("$key: $value\r\n".toByteArray())
            }
            if(socket.isClosed()) return
            socket.getOutputStream().write("\r\n".toByteArray())
            sendHeaders = true
        }
    }

    fun write(p0: Int) {
        if(socket.isClosed()) return
        sendHeaders()
        socket.getOutputStream().write(p0)
    }

    fun write(bytes:ByteArray){
        write(bytes,0,bytes.size)
    }

    fun write(bytes: ByteArray, offset:Int, length:Int) {
        if (bytes.size > 0) {
            var c = offset
            while (c < length) {
                write(bytes[c].toInt())
                c++
            }
        }
    }

    fun print(msg: String) {
        write(msg.toByteArray())
    }

    fun println(msg: String) {
        print("$msg\r\n")
    }

    fun printf(format: String, str: String) {
        print(String.format(format, str))
    }

    fun printError(msg: String) {
        if (status > 200) { //Not printing error with code 200 OK
            contentType("text/html")
            println("<html><head><title>$status ${status_msg.get(status)}</title></head><body><h2>$status ${status_msg.get(status)}</h2>$msg<hr>${Main.instance()}</body></html>")
        }
    }

    private fun flush() {
        if(socket.isClosed()) return
        sendHeaders()
        socket.getOutputStream().flush()
    }

    fun close() {
        if(socket.isClosed()) return
        flush()
        socket.getOutputStream().close()
    }
}