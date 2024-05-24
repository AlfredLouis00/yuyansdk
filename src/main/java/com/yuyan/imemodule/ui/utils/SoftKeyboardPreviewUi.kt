package com.yuyan.imemodule.ui.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.singleton.EnvironmentSingleton.Companion.instance
import com.yuyan.imemodule.utils.LogUtil.d
import com.yuyan.imemodule.view.CandidatesBar
import com.yuyan.imemodule.view.keyboard.container.QwertyTextContainer

class SoftKeyboardPreviewUi(context: Context) : RelativeLayout(context) {
    var intrinsicWidth = 0
        private set
    var intrinsicHeight = 0
        private set
    private var qwerTextContainer: QwertyTextContainer? = null
    private fun initView() {
        d("SoftKeyboardPreviewUi", "initView")
        removeAllViews()
        val mSkbRoot = LayoutInflater.from(context).inflate(R.layout.sdk_skb_container, this, false)
        val previewUi = mSkbRoot.findViewById<RelativeLayout>(R.id.skb_input_keyboard_view)
        qwerTextContainer = QwertyTextContainer(context)
        qwerTextContainer!!.updateSkbLayout(InputModeSwitcherManager.MASK_SKB_LAYOUT_QWERTY_PINYIN)
        val skbCandidatesBarView = mSkbRoot.findViewById<CandidatesBar>(R.id.candidates_bar)
        skbCandidatesBarView.initialize(null, null) // 刷新下拉箭头位置
        previewUi.addView(qwerTextContainer)
        addView(mSkbRoot)
        val oneHandedMod = prefs.oneHandedMod.getValue()
        if (oneHandedMod != KeyboardOneHandedMod.None) {
            val mHoderLayout = LayoutInflater.from(context)
                .inflate(R.layout.sdk_skb_holder_layout, this, false) as LinearLayout
            val mIbOneHand = mHoderLayout.findViewById<ImageButton>(R.id.ib_holder_one_hand_left)
            val margin = instance!!.heightForCandidates * 2
            val layoutParamsHoder = LayoutParams(
                instance!!.holderWidth, instance!!.skbHeight - margin
            )
            layoutParamsHoder.setMargins(0, margin, 0, margin)
            val layoutParams = mSkbRoot.layoutParams as LayoutParams
            if (oneHandedMod == KeyboardOneHandedMod.LEFT) {
                mIbOneHand.setImageResource(R.drawable.sdk_vector_menu_skb_one_hand_right)
                layoutParams.setMargins(0, 0, instance!!.holderWidth, 0)
                layoutParamsHoder.addRule(ALIGN_PARENT_RIGHT, TRUE)
            } else if (oneHandedMod == KeyboardOneHandedMod.RIGHT) {
                mIbOneHand.setImageResource(R.drawable.sdk_vector_menu_skb_one_hand)
                layoutParams.setMargins(instance!!.holderWidth, 0, 0, 0)
                layoutParamsHoder.addRule(ALIGN_PARENT_LEFT, mSkbRoot.id)
            }
            mSkbRoot.setLayoutParams(layoutParams)
            addView(mHoderLayout, layoutParamsHoder)
        } else {
            val layoutParams = mSkbRoot.layoutParams as LayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
        }
        intrinsicWidth = instance!!.skbWidth
        intrinsicHeight = instance!!.skbHeight
        previewUi.requestLayout()
    }

    fun setTheme(theme: Theme, background: Drawable?) {
        d("SoftKeyboardPreviewUi", "initView  setTheme")
        qwerTextContainer!!.setTheme(theme)
        if (background != null) {
            setBackground(background)
        } else {
            val keyBorder = prefs.keyBorder.getValue()
            setBackground(theme.backgroundDrawable(keyBorder))
        }
    }

    fun setTheme(theme: Theme) {
        d("SoftKeyboardPreviewUi", "initView  setTheme2")
        initView()
        qwerTextContainer!!.setTheme(theme)
        val keyBorder = prefs.keyBorder.getValue()
        background = theme.backgroundDrawable(keyBorder)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (onSizeMeasured != null) onSizeMeasured!!.invoke(intrinsicWidth, intrinsicHeight)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (onSizeMeasured != null) onSizeMeasured!!.invoke(intrinsicWidth, intrinsicHeight)
    }

    var onSizeMeasured: Function2<Int, Int, Unit>? = null

    init {
        initView()
    }
}