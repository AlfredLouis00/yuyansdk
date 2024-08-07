
package com.yuyan.imemodule.view.popup

import android.graphics.Rect
import com.yuyan.imemodule.callback.IResponseKeyEvent

sealed class PopupAction {

    abstract val viewId: Int

    data class PreviewAction(
        override val viewId: Int,
        val content: String,
        val bounds: Rect
    ) : PopupAction()

    data class PreviewUpdateAction(
        override val viewId: Int,
        val content: String,
    ) : PopupAction()

    data class DismissAction(
        override val viewId: Int
    ) : PopupAction()

    data class ShowKeyboardAction(
        override val viewId: Int,
        val keyboard: KeyDef.Popup.Keyboard,
        val bounds: Rect
    ) : PopupAction()

    data class ChangeFocusAction(
        override val viewId: Int,
        val x: Float,
        val y: Float
    ) : PopupAction()

    data class TriggerAction(
        override val viewId: Int,
        var service: IResponseKeyEvent? = null
    ) : PopupAction()
}
