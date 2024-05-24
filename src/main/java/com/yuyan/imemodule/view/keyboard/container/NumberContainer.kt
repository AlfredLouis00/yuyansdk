package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.adapter.PrefixAdapter
import com.yuyan.imemodule.entity.keyboard.SoftKey
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.utils.DevicesUtils.tryPlayKeyDown
import com.yuyan.imemodule.utils.DevicesUtils.tryVibrate
import com.yuyan.imemodule.utils.KeyboardLoaderUtil.Companion.instance
import com.yuyan.imemodule.view.keyboard.TextKeyboard

class NumberContainer(context: Context?) : InputBaseContainer(context) {
    // 键盘、候选词界面上符号(T9左侧、手写右侧)、候选拼音ListView
    private var mRVLeftPrefix : RecyclerView = inflate(getContext(), R.layout.sdk_view_rv_prefix, null) as RecyclerView

    /**
     * 更新软键盘布局
     */
    override fun updateSkbLayout(skbValue: Int) {
        if (null == mMajorView) {
            mMajorView = TextKeyboard(context)
            val params: ViewGroup.LayoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addView(mMajorView, params)
            mMajorView!!.setResponseKeyEvent(inputView)
        }
        val softKeyboard = instance!!.getSoftKeyboard(skbValue)
        mMajorView!!.setSoftKeyboard(softKeyboard)
        updateKeyboardView()
        mMajorView!!.invalidate()
    }

    // 更新键盘上侧边符号列表
    protected fun updateKeyboardView() {
        val prefixLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRVLeftPrefix.setLayoutManager(prefixLayoutManager)
        if (mRVLeftPrefix.parent != null) {
            val parent = mRVLeftPrefix.parent as ViewGroup
            parent.removeView(mRVLeftPrefix)
        }
        addView(mRVLeftPrefix, createLayoutParams())
        val strs = resources.getStringArray(R.array.SymbolRealNumber)
        val adapter = PrefixAdapter(context, strs)
        adapter.setOnItemClickLitener { view: RecyclerView.Adapter<*>?, v: View?, position: Int ->
            val softKey = SoftKey(strs[position])
            inputView!!.responseKeyEvent(softKey)
            tryPlayKeyDown()
            tryVibrate(this)
        }
        mRVLeftPrefix.setAdapter(adapter)
    }

    private fun createLayoutParams(): LayoutParams {
        val softKeyboard = mMajorView!!.getSoftKeyboard()
        val softKeySymbolHolder =
            softKeyboard.getKeyByCode(InputModeSwitcherManager.USERDEF_KEYCODE_LEFT_SYMBOL_12)
        val prefixLayoutParams = LayoutParams(
            softKeySymbolHolder!!.width(), LayoutParams.MATCH_PARENT
        )
        prefixLayoutParams.setMargins(
            softKeyboard.keyXMargin,
            softKeySymbolHolder.mTop + softKeyboard.keyYMargin,
            softKeyboard.keyXMargin,
            softKeyboard.skbCoreHeight - softKeySymbolHolder.mBottom + softKeyboard.keyYMargin
        )
        return prefixLayoutParams
    }
}