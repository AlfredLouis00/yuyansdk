package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.adapter.ClipBoardAdapter
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.entity.ClipBoardDataBean
import com.yuyan.imemodule.view.keyboard.InputView
import com.yuyan.inputmethod.core.CandidateListItem

class ClipBoardContainer(context: Context, inputView: InputView?) : BaseContainer(context, inputView) {
    private var mRVSymbolsView: RecyclerView? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        mRVSymbolsView = RecyclerView(context)
        mRVSymbolsView!!.setHasFixedSize(true)
        mRVSymbolsView!!.setItemAnimator(null)
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRVSymbolsView!!.setLayoutManager(manager)
        val layoutParams2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mRVSymbolsView!!.setLayoutParams(layoutParams2)
        this.addView(mRVSymbolsView)
        showClipBoardView()
    }

    /**
     * 显示候选词界面 , 点击候选词时执行
     */
    fun showClipBoardView() {
        val copyContents : List<ClipBoardDataBean> = LauncherModel.instance.mClipboardDao?.getAllClipboardContent("") ?: return
        val words = ArrayList<CandidateListItem?>()
        for (clipBoardDataBean in copyContents) {
            val copyContent = clipBoardDataBean.copyContent
            if(!copyContent.isNullOrEmpty()) {
                words.add(CandidateListItem("", copyContent))
            }
        }
        inputView?.responseClipboardResultEvent(words)
        val adapter = ClipBoardAdapter(context, copyContents)
        adapter.setOnItemClickLitener { parent: RecyclerView.Adapter<*>?, _: View?, position: Int ->
            if (parent is ClipBoardAdapter) {
                inputView!!.onChoiceTouched(position)
            }
        }
        mRVSymbolsView!!.setAdapter(adapter)
    }

    companion object {
        private val TAG = CandidatesContainer::class.java.getSimpleName()
    }
}