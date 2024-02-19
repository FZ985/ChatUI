package io.im.kit.widget.switchpanel.interfaces

interface PanelHeightMeasurer {
    fun synchronizeKeyboardHeight(): Boolean
    fun forceUseTargetPanelDefaultHeight(): Boolean
    fun getTargetPanelDefaultHeight(): Int
    fun getPanelTriggerId(): Int
}

private typealias GetTargetPanelDefaultHeight = () -> Int
private typealias GetPanelId = () -> Int
private typealias SynchronizeKeyboardHeight = () -> Boolean
private typealias ForceUseTargetPanelDefaultHeight = () -> Boolean

open class PanelHeightMeasurerBuilder : PanelHeightMeasurer {

    private var getPanelDefaultHeight: GetTargetPanelDefaultHeight? = null
    private var getPanelId: GetPanelId? = null
    private var synchronizeKeyboardHeight: SynchronizeKeyboardHeight? = null
    private var forceUseTargetPanelDefaultHeight: ForceUseTargetPanelDefaultHeight? = null

    override fun getTargetPanelDefaultHeight(): Int = getPanelDefaultHeight?.invoke() ?: 0

    override fun getPanelTriggerId(): Int = getPanelId?.invoke() ?: -1

    override fun synchronizeKeyboardHeight(): Boolean = synchronizeKeyboardHeight?.invoke() ?: true

    override fun forceUseTargetPanelDefaultHeight(): Boolean =
        forceUseTargetPanelDefaultHeight?.invoke() ?: false


    open fun getTargetPanelDefaultHeight(getPanelDefaultHeight: GetTargetPanelDefaultHeight) {
        this.getPanelDefaultHeight = getPanelDefaultHeight
    }

    open fun getPanelTriggerId(getPanelId: GetPanelId) {
        this.getPanelId = getPanelId
    }

    open fun synchronizeKeyboardHeight(synchronizeKeyboardHeight: SynchronizeKeyboardHeight) {
        this.synchronizeKeyboardHeight = synchronizeKeyboardHeight
    }

    open fun forceUseTargetPanelDefaultHeight(forceUseTargetPanelDefaultHeight: ForceUseTargetPanelDefaultHeight) {
        this.forceUseTargetPanelDefaultHeight = forceUseTargetPanelDefaultHeight
    }

}