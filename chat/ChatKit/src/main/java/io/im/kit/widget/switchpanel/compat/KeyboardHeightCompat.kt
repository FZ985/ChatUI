package io.im.kit.widget.switchpanel.compat

import io.im.kit.widget.switchpanel.utils.RomUtils
import java.util.Locale

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/11/4
 * desc   :
 * version: 1.0
 */
object KeyboardHeightCompat {


    fun getMinLimitHeight(): Int {
        if (hasPhysicsKeyboard()) {
            return 100
        }
        return 300
    }

    fun hasPhysicsKeyboard(): Boolean {
        val rom = RomUtils.getRomInfo()
        if (rom.name == "blackberry") {
            return rom.model?.toLowerCase(Locale.ROOT)?.contains("bbf100") ?: false
        }
        return false
    }


    fun panelDefaultHeight(defaultHeight : Int): Int {
        if (hasPhysicsKeyboard()) {
            return 705
        }
        return defaultHeight
    }


}