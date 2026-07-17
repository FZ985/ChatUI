package io.im.app

import android.util.Log
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger


/**
 * by DAD FZ
 * 2026/7/16
 * desc：请求id生成方式
 **/
object V1RequestIdGenerator {

    private const val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val INDEX = IntArray(128) { -1 }.apply {
        CHARS.forEachIndexed { index, c ->
            this[c.code] = index
        }
    }

    private val BASE = BigInteger.valueOf(62)

    private val seq = AtomicInteger(0)


    /**
     * 生成id
     * 生成规则：
     *  1、100-999三位随机数
     *  2、毫秒级时间戳
     *  3、0-255自增序列(不足三位前面补0)
     *  4、用户id字符串转数字 或 转byteArray
     *  四个参数依次拼接使用base62 encode生成
     *  android、ios、js、php四端编解码通用
     */
    fun generate(uid: String, useByteArray: Boolean): String {
        val sb = StringBuilder()

        val ts = System.currentTimeMillis()

        val random = (100..999).random()

        val sequence = getSequence()

        sb.append(random)

        sb.append(ts)

        sb.append(sequence)

        if (useByteArray) {
            sb.append(uid)
        } else {
            sb.append(bkdrHash(uid))
        }

        log("拼接输出:" + sb.toString())

        val num = if (useByteArray) BigInteger(
            1,
            sb.toString().toByteArray(Charsets.UTF_8)
        ) else sb.toString().toBigInteger()

        val requestId = encode(num)
        log("编码输出:" + requestId)
        return requestId
    }

    //字符串转数字
    private fun bkdrHash(str: String): Long {
        var hash = 0L
        for (c in str) {
            hash = hash * 131 + c.code
            hash = hash and 0xffffffffL
        }
        return hash
    }

    //0-255自增序列，不够三位前面补0
    private fun getSequence(): String {
        val sb = StringBuilder()
        val sequence = seq.getAndIncrement() and 0xFF // 0~255
        val seqLen = 3 - (sequence.toString().length)
        (0 until seqLen).forEach { _ ->
            sb.append(0)
        }
        sb.append(sequence)
        return sb.toString()
    }

    private fun log(m: String) {
        Log.e("V1RequestIdGenerator", m)
    }


    private fun encode(value: BigInteger): String {
        if (value == BigInteger.ZERO) return "0"
        var num = value
        val sb = StringBuilder()
        while (num > BigInteger.ZERO) {
            val divRem = num.divideAndRemainder(BASE)
            sb.append(CHARS[divRem[1].toInt()])
            num = divRem[0]
        }
        return sb.reverse().toString()
    }


    fun decode(str: String, useByteArray: Boolean): String {
        if (useByteArray) {
            return decodeWithBigIntByteArray(str)
        }
        return decodeWithBigInt(str).toString()
    }

    private fun decodeWithBigInt(str: String): BigInteger {
        var result = BigInteger.ZERO
        for (c in str) {
            val index = if (c.code < INDEX.size) INDEX[c.code] else -1
            require(index >= 0) { "Invalid Base62 character: $c" }
            result = result.multiply(BASE)
                .add(BigInteger.valueOf(index.toLong()))
        }
        return result
    }

    private fun decodeWithBigIntByteArray(str: String): String {
        val bytes = decodeWithBigInt(str).toByteArray()
        val realBytes =
            if (bytes.firstOrNull() == 0.toByte())
                bytes.copyOfRange(1, bytes.size)
            else
                bytes

        val raw = String(realBytes, Charsets.UTF_8)
        return raw
    }


    fun test() {
        val useByteArray = false

        val id1 = generate("35", useByteArray)
        val r1 = decode(id1, useByteArray)
        log("1解码输出:" + r1)

        val id2 = generate("d92kd", useByteArray)
        val r2 = decode(id2, useByteArray)
        log("2解码输出:" + r2)


        val id3 = generate("1", useByteArray)
        val r3 = decode(id3, useByteArray)
        log("3解码输出:" + r3)

    }

}