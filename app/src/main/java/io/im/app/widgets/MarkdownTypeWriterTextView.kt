package io.im.app.widgets

import android.content.Context
import android.text.Spanned
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
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

// 需要哪些语言就写在这里
@PrismBundle(
    include = ["kotlin", "java", "python", "javascript", "go", "json", "markdown"]
)
class PrismGrammarGen

class MarkdownTypeWriterTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    val prism4j = Prism4j(GrammarLocatorDef())

    //浅色主题；深色用 Prism4jThemeDarkula.create()
    val prismTheme = Prism4jThemeDefault.create()

    private val markwon: Markwon = Markwon.builder(context)
        .usePlugin(SyntaxHighlightPlugin.create(prism4j, prismTheme))
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(TablePlugin.create {
            it.tableBorderWidth(2)
            it.tableCellPadding(-10)
        })
        .build()

    private var typingJob: Job? = null
    private var rawMarkdown = "" // 原始完整markdown
    private var delayMs = 30L
    var isPlaying = false
        private set
    private var playPos = 0

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * 设置完整内容，不播放动画；恢复状态用，同步playPos
     */
    fun setNoAnimate(text: String) {
        stopTyping()
        rawMarkdown = text
        playPos = text.length
        setRenderText(rawMarkdown.take(playPos))
    }

    /**
     * 从头开始打字动画
     */
    fun startMarkdownTypeWriter(fullMarkdown: String) {
        startMarkdownTypeWriter(fullMarkdown, startPos = 0)
    }

    /**
     * 接续打字：从startPos继续往后输出markdown文本
     * @param fullMarkdown 当前完整的markdown原始字符串（SSE累加后的完整值）
     * @param startPos 从哪个下标继续
     */
    fun startMarkdownTypeWriter(fullMarkdown: String, startPos: Int) {
        stopTyping()
        rawMarkdown = fullMarkdown
        val safeStartPos = startPos.coerceIn(0, rawMarkdown.length)
        playPos = safeStartPos
        isPlaying = true

        // 先渲染已经打完的部分
        setRenderText(rawMarkdown.take(playPos))

        typingJob = CoroutineScope(Dispatchers.Main).launch {
            for (i in playPos until rawMarkdown.length) {
                playPos = i + 1
                val currentText = rawMarkdown.take(playPos)
                setRenderText(currentText)
                delay(delayMs)
            }
            isPlaying = false
        }
    }

    /**
     * 渲染文本：尝试Markdown解析，失败降级纯文本
     */
    private fun setRenderText(text: String) {
        try {
            val spanned: Spanned = markwon.toMarkdown(text)
            this.text = spanned
        } catch (e: Exception) {
            // markdown语法残缺解析失败，降级原始文本
            this.text = text
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

    fun getRawMarkdown(): String = rawMarkdown
    fun getPlayPosition(): Int = playPos

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopTyping()
    }
}