package io.im.kit.widget.switchpanel.interfaces

/**
 * --------------------
 * | PanelSwitchLayout  |
 * |  ----------------  |
 * | |                | |
 * | |ContentContainer| |
 * | |                | |
 * |  ----------------  |
 * |  ----------------  |
 * | | PanelContainer | |
 * |  ----------------  |
 * --------------------
 * There are some rules that must be processed:
 *
 * 1. [io.im.kit.widget.switchpanel.view.PanelSwitchLayout] must have only two children
 * [io.im.kit.widget.switchpanel.view.content.IContentContainer] and [io.im.kit.widget.switchpanel.view.PanelContainer]
 *
 * 2. [io.im.kit.widget.switchpanel.view.content.IContentContainer] must set "edit_view" value to provide [android.widget.EditText]
 *
 * 3. [io.im.kit.widget.switchpanel.view.PanelContainer] has some Children that are [io.im.kit.widget.switchpanel.view.PanelView]
 * [io.im.kit.widget.switchpanel.view.PanelView] must set "panel_layout" value to provide panelView and set "panel_trigger"  value to
 * specify layout for click to checkout panelView
 *
 * Created by yummyLau on 18-7-10
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
interface ViewAssertion {
    fun assertView()
}