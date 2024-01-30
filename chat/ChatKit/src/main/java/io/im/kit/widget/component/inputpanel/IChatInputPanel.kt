package io.im.kit.widget.component.inputpanel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.im.kit.databinding.KitComponentInputPanelBinding


/**
 *  author : JFZ
 *  date : 2024/1/30 13:56
 *  description :
 */
class IChatInputPanel : FrameLayout {

    private var binding: KitComponentInputPanelBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        binding = KitComponentInputPanelBinding.inflate(LayoutInflater.from(context), this, true)
    }
}