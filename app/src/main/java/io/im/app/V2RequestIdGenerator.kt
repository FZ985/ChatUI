package io.im.app

import android.util.Log
import java.math.BigInteger
import java.security.SecureRandom


/**
 * by DAD FZ
 * 2026/7/16
 * desc：请求id生成方式
 **/
object V2RequestIdGenerator {

    data class Parsed(
        val timestamp: Long,
        val random: Int
    ) {
        override fun toString(): String {
            return "(timestamp=$timestamp, random=$random)"
        }
    }


    private const val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    private val INDEX = IntArray(128) { -1 }.apply {
        CHARS.forEachIndexed { index, c ->
            this[c.code] = index
        }
    }

    private val BASE = BigInteger.valueOf(62)


    private const val RANDOM_BITS = 22
    private const val RANDOM_MASK = (1L shl RANDOM_BITS) - 1

    //推荐使用 SecureRandom，不可预测
    private val random = SecureRandom()

    /**
     * 生成 63 位 RequestId
     * 41 bit：毫秒时间戳（System.currentTimeMillis）
     * 22 bit：随机数
     */
    fun nextLong(): Long {
        val timestamp = System.currentTimeMillis()
        //保证只取41bit
        val ts = timestamp and ((1L shl 41) - 1)
        val random = random.nextLong() and RANDOM_MASK
        return (ts shl RANDOM_BITS) or random
    }

    /**
     * Base62 RequestId
     */
    fun nextBase62(): String {
        return encode(BigInteger.valueOf(nextLong()))
    }

    /**
     * 解码
     */
    fun parse(id: Long): Parsed {
        val random = (id and RANDOM_MASK).toInt()
        val timestamp = id ushr RANDOM_BITS
        return Parsed(
            timestamp = timestamp,
            random = random
        )
    }

    /**
     * 解码
     */
    fun parseBase62(idStr: String): Parsed {
        return parse(decode(idStr).toLong())
    }

    private fun log(m: String) {
        Log.e("V2RequestIdGenerator", m)
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


    private fun decode(str: String): BigInteger {
        var result = BigInteger.ZERO
        for (c in str) {
            val index = if (c.code < INDEX.size) INDEX[c.code] else -1
            require(index >= 0) { "Invalid Base62 character: $c" }
            result = result.multiply(BASE)
                .add(BigInteger.valueOf(index.toLong()))
        }
        return result
    }


    fun test() {
        val id1 = nextLong()
        val r1 = parse(id1)
        log("1编码输出:" + id1)
        log("1解码输出:" + r1)

        val id2 = nextBase62()
        log("2编码输出:" + id2)
        val r2 = parseBase62(id2)
        log("2解码输出:" + r2)

        val id3 = nextLong()
        log("3编码输出：" + id3)
        val r3 = parse(id3)
        log("3解码输出：" + r3)
    }

}