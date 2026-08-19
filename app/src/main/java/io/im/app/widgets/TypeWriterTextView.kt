package io.im.app.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * by DAD FZ
 * 2026/8/19
 * desc：
 **/
class TypeWriterTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var typingJob: Job? = null
    private var fullContent = ""
    private var delayMs = 30L
    var isPlaying = false
        private set

    // 记录当前打字播放到的位置，实现接续播放，不从头重来
    private var playPos = 0

    /** 增量追加（SSE分片调用）*/
    fun appendStreamText(delta: CharSequence) {
        fullContent += delta
        setText(fullContent)
    }

    /** 完整文本直接设置，不播放动画（历史消息、已完成消息） */
    fun setFullTextNoAnimate(text: String) {
        stopTyping()
        fullContent = text
        playPos = text.length // 直接跳到末尾，后续接续不会重放
        setText(fullContent)
    }

    /** 开始打字动画，**仅用于正在生成的新消息** */
    fun startTypeWriter(text: String) {
        startTypeWriter(text, startPos = 0)
    }

    /**
     * 【核心重载】支持从指定位置接续打字，不会从头重放
     * @param text 完整目标文本
     * @param startPos 从哪个下标开始输出，一般传入上一次的playPos
     */
    fun startTypeWriter(text: String, startPos: Int) {
        stopTyping()

        fullContent = text
        // 边界保护，不能小于0，不能超过文本总长度
        val safeStartPos = startPos.coerceIn(0, text.length)
        playPos = safeStartPos

        isPlaying = true
        setText(fullContent.take(playPos)) // 先把已经打完的部分直接显示

        typingJob = CoroutineScope(Dispatchers.Main).launch {
            // 从playPos往后继续输出新增字符
            for (i in playPos until fullContent.length) {
                playPos = i + 1
                setText(fullContent.take(playPos))
                delay(delayMs)
            }
            isPlaying = false
        }
    }

    fun stopTyping() {
        typingJob?.cancel()
        typingJob = null
        isPlaying = false
    }

    fun setTypingDelay(delay: Long) {
        delayMs = delay
    }

    fun getFullText(): String = fullContent

    /** 获取当前已经播放到的位置，外部保存到model的displayedLength */
    fun getPlayPosition(): Int = playPos

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // item滑出屏幕，立刻停止打字，防止协程泄漏
        stopTyping()
    }
}