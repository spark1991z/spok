package project.crypto

import java.security.MessageDigest

/**
 * @author spark1991z
 */
class MD5 private constructor() {

    companion object {

        fun digest(str: String): String {
            var ba: ByteArray = digest(str.toByteArray())
            var md5n: String = ""
            for (i in ba) {
                md5n += Integer.toHexString((i.toInt() and 0xff))
            }
            return md5n;
        }

        fun digest(bytes: ByteArray): ByteArray {
            var md: MessageDigest = MessageDigest.getInstance("MD5")
            md.reset()
            return md.digest(bytes)
        }
    }
}