package com.yuyan.imemodule.view.keyboard.container

import android.content.Context
import android.graphics.drawable.VectorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.constant.CustomConstant
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.data.theme.ThemeManager.prefs
import com.yuyan.imemodule.data.theme.ThemeManager.setNormalModeTheme
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.manager.SymbolsManager
import com.yuyan.imemodule.prefs.AppPrefs.Companion.getInstance
import com.yuyan.imemodule.prefs.behavior.KeyboardOneHandedMod
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.ui.utils.AppUtil.launchSettings
import com.yuyan.imemodule.ui.utils.AppUtil.launchSettingsToHandwriting
import com.yuyan.imemodule.ui.utils.AppUtil.launchSettingsToKeyboard
import com.yuyan.imemodule.utils.DevicesUtils.dip2px
import com.yuyan.inputmethod.core.Kernel
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.utils.LogUtil.d
import com.yuyan.imemodule.view.keyboard.KeyboardManager
import com.yuyan.imemodule.view.skbitem.PageMenuLayout
import com.yuyan.imemodule.view.skbitem.adapter.EntranceAdapter
import com.yuyan.imemodule.view.skbitem.holder.AbstractHolder
import com.yuyan.imemodule.view.skbitem.holder.PageMenuViewHolderCreator
import com.yuyan.imemodule.view.skbitem.widget.IndicatorView
import java.util.LinkedList

class SettingsContainer(context: Context) : BaseContainer(context) {
    private var mPageMenuLayout: PageMenuLayout<SkbFunItem>? = null
    private var mIndicatorView: IndicatorView? = null
    private var mTheme: Theme? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        mTheme = activeTheme
        mPageMenuLayout = PageMenuLayout(context)
        mPageMenuLayout!!.setRowCount(2)
        mPageMenuLayout!!.setSpanCount(4)
        mIndicatorView = IndicatorView(context)
        mIndicatorView!!.setIndicatorWidth(10)
        mIndicatorView!!.setIndicatorColor(mTheme!!.keyTextColor)
        mIndicatorView!!.setIndicatorColorSelected(mTheme!!.genericActiveBackgroundColor)
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            dip2px(12f)
        )
        layoutParams.addRule(ALIGN_PARENT_BOTTOM)
        mIndicatorView!!.setLayoutParams(layoutParams)
        this.addView(mIndicatorView)
        val layoutParams2 = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        layoutParams2.addRule(ABOVE, mIndicatorView!!.id)
        mPageMenuLayout!!.setLayoutParams(layoutParams2)
        this.addView(mPageMenuLayout)
    }

    /**
     * 弹出键盘设置界面
     */
    fun showSettingsView() {
        //获取键盘功能栏功能对象
        val funItems: MutableList<SkbFunItem> = LinkedList()
        //        funItems.add(new SkbFunItem(mContext.getString(R.string.emoji_setting), R.drawable.sdk_vector_menu_skb_emoji, SkbMenuMode.EmojiKeyboard));
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.changeKeyboard),
                R.drawable.sdk_vector_menu_skb_keyboard,
                SkbMenuMode.SwitchKeyboard
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.setting_ime_keyboard_height),
                R.drawable.sdk_vector_menu_skb_height,
                SkbMenuMode.KeyboardHeight
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_theme_night),
                R.drawable.sdk_vector_menu_skb_dark,
                SkbMenuMode.DarkTheme
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_feedback),
                R.drawable.sdk_vector_menu_skb_touch,
                SkbMenuMode.Feedback
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_one_handed_mod),
                R.drawable.sdk_vector_menu_skb_one_hand,
                SkbMenuMode.OneHanded
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.engish_full_keyboard),
                R.drawable.sdk_vector_menu_skb_shuzihang,
                SkbMenuMode.NumberRow
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.setting_jian_fan),
                R.drawable.sdk_vector_menu_skb_fanti,
                SkbMenuMode.JianFan
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_mnemonic_show),
                R.drawable.sdk_vector_menu_skb_mnemonic,
                SkbMenuMode.Mnemonic
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.skb_item_settings),
                R.drawable.sdk_vector_menu_skb_setting,
                SkbMenuMode.Settings
            )
        )
        mPageMenuLayout!!.setPageDatas(funItems, object : PageMenuViewHolderCreator<SkbFunItem> {
            override fun createHolder(itemView: View?): AbstractHolder<SkbFunItem> {
                return object : AbstractHolder<SkbFunItem>(itemView!!) {
                    private var entranceNameTextView: TextView? = null
                    private var entranceIconImageView: ImageView? = null
                    override fun initView(itemView: View?) {
                        entranceIconImageView = itemView!!.findViewById(R.id.entrance_image)
                        entranceNameTextView = itemView.findViewById(R.id.entrance_name)
                        val screenWidth =
                            ImeSdkApplication.context.resources.displayMetrics.widthPixels
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (screenWidth.toFloat() / 4.0f).toInt()
                        )
                        itemView.setLayoutParams(layoutParams)
                    }

                    override fun bindView(
                        adapter: EntranceAdapter<SkbFunItem>?,
                        holder: RecyclerView.ViewHolder?,
                        data: SkbFunItem,
                        pos: Int
                    ) {
                        entranceNameTextView!!.text = data.funName
                        entranceIconImageView!!.setImageResource(data.funImgRecource)
                        val color =
                            if (isSettingsMunuSelect(data)) mTheme!!.genericActiveBackgroundColor else mTheme!!.keyTextColor
                        entranceNameTextView!!.setTextColor(color)
                        val vectorDrawableCompat =
                            entranceIconImageView!!.getDrawable() as VectorDrawable
                        vectorDrawableCompat.setTint(color)
                        holder!!.itemView.setOnClickListener { view: View? ->
                            onSettingsMenuClick(
                                data
                            )
                        }
                    }
                }
            }

            override fun getLayoutId(): Int {
                return R.layout.sdk_item_skb_menu_entrance
            }
        })
        mIndicatorView!!.setIndicatorCount(mPageMenuLayout!!.pageCount)
        mIndicatorView!!.setCurrentIndicator(0)
        mIndicatorView!!.setCurrentIndicator(0)
        mPageMenuLayout!!.setOnPageListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                mIndicatorView!!.setCurrentIndicator(position)
            }
        })
    }

    private fun onSettingsMenuClick(data: SkbFunItem) {
        when (data.skbMenuMode) {
            SkbMenuMode.EmojiKeyboard -> {
                val symbols =
                    SymbolsManager.instance!!.getmSymbols(CustomConstant.EMOJI_TYPR_FACE_DATA)
                inputView!!.showSymbols(symbols)
                KeyboardManager.instance!!.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                (KeyboardManager.instance!!.currentContainer as SymbolContainer?)!!.setSymbolsView(
                    CustomConstant.EMOJI_TYPR_FACE_DATA
                )
            }
            SkbMenuMode.SwitchKeyboard -> (KeyboardManager.instance!!.currentContainer as SettingsContainer?)!!.showSkbSelelctModeView()
            SkbMenuMode.KeyboardHeight -> {
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
                (KeyboardManager.instance!!.currentContainer as InputBaseContainer?)!!.setKeyboardHeight()
            }
            SkbMenuMode.DarkTheme -> {
                val isDark = activeTheme.isDark
                val theme: Theme = if (isDark) {
                    prefs.lightModeTheme.getValue()
                } else {
                    prefs.darkModeTheme.getValue()
                }
                setNormalModeTheme(theme)
                KeyboardManager.instance!!.clearKeyboard()
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
            }
            SkbMenuMode.Feedback -> {
                launchSettingsToKeyboard(mContext)
            }
            SkbMenuMode.NumberRow -> {
                val abcNumberLine = prefs.abcNumberLine.getValue()
                prefs.abcNumberLine.setValue(!abcNumberLine)
                //更换键盘模式后 重亲加载键盘
                KeyboardLoaderUtil.instance!!.changeSKBNumberRow(!abcNumberLine)
                KeyboardManager.instance!!.clearKeyboard()
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
            }
            SkbMenuMode.JianFan -> {
                val chineseFanTi = getInstance().input.chineseFanTi.getValue()
                getInstance().input.chineseFanTi.setValue(!chineseFanTi)
                Kernel.nativeUpdateImeOption()
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
            }
            SkbMenuMode.LockEnglish -> {
                val keyboardLockEnglish = prefs.keyboardLockEnglish.getValue()
                prefs.keyboardLockEnglish.setValue(!keyboardLockEnglish)
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
            }
            SkbMenuMode.SymbolShow -> {
                val keyboardSymbol = prefs.keyboardSymbol.getValue()
                prefs.keyboardSymbol.setValue(!keyboardSymbol)
                KeyboardManager.instance!!.clearKeyboard()
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
            }
            SkbMenuMode.Mnemonic -> {
                val keyboardMnemonic = prefs.keyboardMnemonic.getValue()
                prefs.keyboardMnemonic.setValue(!keyboardMnemonic)
                KeyboardManager.instance!!.clearKeyboard()
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
            }
            SkbMenuMode.EmojiInput -> {
                val emojiInput = getInstance().input.emojiInput.getValue()
                getInstance().input.emojiInput.setValue(!emojiInput)
                Kernel.nativeUpdateImeOption()
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
            }
            SkbMenuMode.Handwriting -> launchSettingsToHandwriting(mContext)
            SkbMenuMode.Settings -> launchSettings(mContext)
            SkbMenuMode.OneHanded -> {
                val oneHandedMod = prefs.oneHandedMod.getValue()
                prefs.oneHandedMod.setValue(if (oneHandedMod == KeyboardOneHandedMod.None) KeyboardOneHandedMod.LEFT else KeyboardOneHandedMod.None)
                EnvironmentSingleton.instance!!.initData()
                KeyboardLoaderUtil.instance!!.clearKeyboardMap()
                KeyboardManager.instance!!.clearKeyboard()
                KeyboardManager.instance!!.switchKeyboard(
                    mInputModeSwitcher!!.skbLayout
                )
            }
            else ->{}
        }
    }

    private fun isSettingsMunuSelect(data: SkbFunItem): Boolean {
        val result: Boolean = when (data.skbMenuMode) {
            SkbMenuMode.DarkTheme -> activeTheme.isDark
            SkbMenuMode.NumberRow -> prefs.abcNumberLine.getValue()
            SkbMenuMode.JianFan -> getInstance().input.chineseFanTi.getValue()
            SkbMenuMode.LockEnglish -> prefs.keyboardLockEnglish.getValue()
            SkbMenuMode.SymbolShow -> prefs.keyboardSymbol.getValue()
            SkbMenuMode.Mnemonic -> prefs.keyboardMnemonic.getValue()
            SkbMenuMode.EmojiInput -> getInstance().input.emojiInput.getValue()
            SkbMenuMode.OneHanded -> prefs.oneHandedMod.getValue() != KeyboardOneHandedMod.None
            else -> false
        }
        return result
    }

    /**
     * 弹出键盘界面
     */
    fun showSkbSelelctModeView() {
        val funItems: MutableList<SkbFunItem> = LinkedList()
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_t9),
                R.drawable.selece_input_mode_py9,
                SkbMenuMode.PinyinT9
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_cn26),
                R.drawable.selece_input_mode_py26,
                SkbMenuMode.Pinyin26Jian
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_hand),
                R.drawable.selece_input_mode_handwriting,
                SkbMenuMode.PinyinHandWriting
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_pinyin_flypy_plus),
                R.drawable.selece_input_mode_py26,
                SkbMenuMode.Pinyin26Double
            )
        )
        funItems.add(
            SkbFunItem(
                mContext.getString(R.string.keyboard_name_pinyin_lx_17),
                R.drawable.selece_input_mode_py26,
                SkbMenuMode.PinyinLx17
            )
        )
        mPageMenuLayout!!.setPageDatas(funItems, object : PageMenuViewHolderCreator<SkbFunItem> {
            override fun createHolder(itemView: View?): AbstractHolder<SkbFunItem> {
                return object : AbstractHolder<SkbFunItem>(itemView!!) {
                    private var entranceNameTextView: TextView? = null
                    private var entranceIconImageView: ImageView? = null
                    override fun initView(itemView: View?) {
                        entranceIconImageView = itemView!!.findViewById(R.id.entrance_image)
                        entranceNameTextView = itemView.findViewById(R.id.entrance_name)
                        val screenWidth =
                            ImeSdkApplication.context.resources.displayMetrics.widthPixels
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (screenWidth.toFloat() / 4.0f).toInt()
                        )
                        itemView.setLayoutParams(layoutParams)
                    }

                    override fun bindView(
                        adapter: EntranceAdapter<SkbFunItem>?,
                        holder: RecyclerView.ViewHolder?,
                        data: SkbFunItem,
                        pos: Int
                    ) {
                        entranceNameTextView!!.text = data.funName
                        entranceIconImageView!!.setImageResource(data.funImgRecource)
                        val color =
                            if (isKeyboardMunuSelect(data)) mTheme!!.genericActiveBackgroundColor else mTheme!!.keyTextColor
                        entranceNameTextView!!.setTextColor(color)
                        val vectorDrawableCompat =
                            entranceIconImageView!!.getDrawable() as VectorDrawable
                        vectorDrawableCompat.setTint(color)
                        holder!!.itemView.setOnClickListener { view: View? ->
                            onKeyboardMenuClick(
                                data
                            )
                        }
                    }
                }
            }

            override fun getLayoutId(): Int {
                return R.layout.sdk_item_skb_menu_entrance
            }
        })
        mIndicatorView!!.setIndicatorCount(mPageMenuLayout!!.pageCount)
        mIndicatorView!!.setCurrentIndicator(0)
        mIndicatorView!!.setCurrentIndicator(0)
        mPageMenuLayout!!.setOnPageListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                mIndicatorView!!.setCurrentIndicator(position)
            }
        })
    }

    private fun onKeyboardMenuClick(data: SkbFunItem) {
        var keyboardValue = 0x2000
        var value = CustomConstant.SCHEMA_ZH_T9
        when (data.skbMenuMode) {
            SkbMenuMode.Pinyin26Jian -> {
                keyboardValue = 0x1000
                value = CustomConstant.SCHEMA_ZH_QWERTY
            }

            SkbMenuMode.PinyinHandWriting -> {
                keyboardValue = 0x3000
                value = CustomConstant.SCHEMA_ZH_HANDWRITING
            }

            SkbMenuMode.Pinyin26Double -> {
                keyboardValue = 0x1000
                value = CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY
            }

            SkbMenuMode.PinyinLx17 -> {
                keyboardValue = 0x6000
                value = CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            }
            else ->{
            }
        }
        val inputMode =
            keyboardValue or InputModeSwitcherManager.MASK_LANGUAGE_CN or InputModeSwitcherManager.MASK_CASE_UPPER
        getInstance().internal.inputMethodPinyinMode.setValue(inputMode)
        getInstance().internal.pinyinModeRime.setValue(value)
        mInputModeSwitcher!!.saveInputMode(inputMode)
        d(TAG, "onKeyboardMenuClick value：$value")
        KeyboardManager.instance!!.switchKeyboard(mInputModeSwitcher!!.skbLayout)
    }

    private fun isKeyboardMunuSelect(data: SkbFunItem): Boolean {
        val rimeValue = getInstance().internal.pinyinModeRime.getValue()
        val result: Boolean = when (data.skbMenuMode) {
            SkbMenuMode.PinyinT9 -> rimeValue == CustomConstant.SCHEMA_ZH_T9
            SkbMenuMode.Pinyin26Jian -> rimeValue == CustomConstant.SCHEMA_ZH_QWERTY
            SkbMenuMode.PinyinHandWriting -> rimeValue == CustomConstant.SCHEMA_ZH_HANDWRITING
            SkbMenuMode.Pinyin26Double -> rimeValue == CustomConstant.SCHEMA_ZH_DOUBLE_FLYPY
            SkbMenuMode.PinyinLx17 -> rimeValue == CustomConstant.SCHEMA_ZH_DOUBLE_LX17
            else -> false
        }
        return result
    }

    companion object {
        private val TAG = SettingsContainer::class.java.getSimpleName()
    }
}