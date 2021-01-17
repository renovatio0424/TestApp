package com.herry.libs.widget.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.*
import androidx.core.view.isVisible
import com.herry.libs.R
import com.herry.libs.widget.extension.*
import kotlin.math.roundToInt

@Suppress("unused", "MemberVisibilityCanBePrivate", "LocalVariableName")
open class AppDialog(activity: Activity?, @StyleRes themeResId: Int = 0, @StyleRes dialogThemeResId: Int = 0) : DialogInterface {

    interface OnClicksListener : DialogInterface.OnClickListener {
        fun onLongClick(dialog: DialogInterface, which: Int) : Boolean
    }

    private var container: FrameLayout? = null
    private var topContainer: FrameLayout? = null

    private var titleContainer: View? = null
    private var titleTextView: TextView? = null
    private var iconImageView: ImageView? = null
    private var topSeparatorView: View? = null

    private var contentsContainer: FrameLayoutEx? = null
    private var messageContainer: View? = null
    private var messageTextView: TextView? = null
    private var messageGapView: View? = null
    private var subMessageTextView: TextView? = null
    private var listView: ListView? = null

    private var bottomContainer: FrameLayout? = null
    private var bottomSeparatorView: View? = null

    private var buttonContainer: LinearLayout? = null

    private var negativeButtonContainer: FrameLayout? = null
    private var neutraButtonContainer: FrameLayout? = null
    private var positiveButtonContainer: FrameLayout? = null
    private var negativeButton: TextView? = null
    private var neutraButton: TextView? = null
    private var positiveButton: TextView? = null
    private var buttonLeftSeparatorView: View? = null
    private var buttonRightSeparatorView: View? = null

    // button listener
    private var positiveButtonOnClickListener: DialogInterface.OnClickListener? = null
    private var negativeButtonOnClickListener: DialogInterface.OnClickListener? = null
    private var neutraButtonOnClickListener: DialogInterface.OnClickListener? = null

    private val nullOnClickListener = DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }
    private var onCancelListener: DialogInterface.OnCancelListener? = null
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private var onShowListener: DialogInterface.OnShowListener? = null
    private var dialog: Dialog? = null
    private var activity: Activity? = null
    private var context: Context? = null

    // view information
    private var dialogWidth = 0
    private var dialogWidthMargin = 0
    private var dialogBackground: Drawable? = null
    private var dialogPaddingStart = 0
    private var dialogPaddingTop = 0
    private var dialogPaddingEnd = 0
    private var dialogPaddingBottom = 0
    private var topBackground: Drawable? = null
    private var topMinHeight = 0
    private var topPaddingStart = 0
    private var topPaddingTop = 0
    private var topPaddingEnd = 0
    private var topPaddingBottom = 0
    private var topDivider: Drawable? = null
    private var topDividerHeight = 0
    private var titleTextSize = 0
    private var titleTextColor: ColorStateList? = null
    private var titleTextStyle = 0
    private var titleTextGravity = 0
    private var titleTextLines = 1
    private var titleTextEllipsize: TextUtils.TruncateAt? = null
    private var titlePaddingStart = 0
    private var titlePaddingTop = 0
    private var titlePaddingEnd = 0
    private var titlePaddingBottom = 0
    private var contentsBackground: Drawable? = null
    private var contentsPaddingStart = 0
    private var contentsPaddingTop = 0
    private var contentsPaddingEnd = 0
    private var contentsPaddingBottom = 0
    private var contentsMinHeight = 0
    private var contentsMaxHeight = 0
    private var messageTextColor: ColorStateList? = null
    private var messageTextSize = 0
    private var messageTextLineSpacingExtra = 0.0f
    private var messageTextGravity = Gravity.CENTER
    private var subMessageTextColor: ColorStateList? = null
    private var subMessageTextSize = 0
    private var subMessageTextLineSpacingExtra = 0.0f
    private var subMessageTextGravity = Gravity.CENTER
    private var messageGapSize = 0
    private var bottomBackground: Drawable? = null
    private var bottomMinHeight = 0
    private var bottomPaddingStart = 0
    private var bottomPaddingTop = 0
    private var bottomPaddingEnd = 0
    private var bottomPaddingBottom = 0
    private var bottomDivider: Drawable? = null
    private var bottomDividerHeight = 0
    private var buttonHeight = 0
    private var buttonWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    private var buttonMinWidth = 0
    private var buttonWeight = 1f
    private var buttonLayoutGravity = Gravity.CENTER
    private var buttonTextSize = 0
    private var buttonTextColor: ColorStateList? = null
    private var buttonTextStyle = 0
    private var buttonTextGravity = Gravity.CENTER
    private var buttonMargin = 0
    private var buttonDivider: Drawable? = null
    private var buttonDividerWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    private var buttonBackground: Drawable? = null
    private var buttonSelectableBackground: Drawable? = null
    private var leftButtonTextSize = 0
    private var leftButtonTextColor: ColorStateList? = null
    private var leftButtonBackground: Drawable? = null
    private var leftButtonIn3ButtonsBackground: Drawable? = null
    private var leftButtonSelectableBackground: Drawable? = null
    private var centerButtonBackground: Drawable? = null
    private var centerButtonSelectableBackground: Drawable? = null
    private var rightButtonTextSize = 0
    private var rightButtonTextColor: ColorStateList? = null
    private var rightButtonBackground: Drawable? = null
    private var rightButtonIn3ButtonsBackground: Drawable? = null
    private var rightButtonSelectableBackground: Drawable? = null
    private var listItemLayout = android.R.layout.simple_list_item_1
    private var singleChoiceItemLayout = android.R.layout.simple_list_item_single_choice

    private val buttonOnClickListener = View.OnClickListener { v ->
        when (v?.id) {
            R.id.app_dialog_button_left_container -> {
                negativeButtonOnClickListener?.onClick(this@AppDialog, BUTTON_NEGATIVE)
            }
            R.id.app_dialog_button_center_container -> {
                neutraButtonOnClickListener?.onClick(this@AppDialog, BUTTON_NEUTRAL)
            }
            R.id.app_dialog_button_right_container -> {
                positiveButtonOnClickListener?.onClick(this@AppDialog, BUTTON_POSITIVE)
            }
        }
    }

    private val buttonOnLongClickListener = View.OnLongClickListener { v ->
        return@OnLongClickListener when (v?.id) {
            R.id.app_dialog_button_left_container -> {
                if (negativeButtonOnClickListener is OnClicksListener) {
                    (negativeButtonOnClickListener as OnClicksListener).onLongClick(this@AppDialog, BUTTON_NEGATIVE)
                } else {
                    false
                }
            }
            R.id.app_dialog_button_center_container -> {
                if (neutraButtonOnClickListener is OnClicksListener) {
                    (neutraButtonOnClickListener as OnClicksListener).onLongClick(this@AppDialog, BUTTON_NEUTRAL)
                } else {
                    false
                }
            }
            R.id.app_dialog_button_right_container -> {
                if (positiveButtonOnClickListener is OnClicksListener) {
                    (positiveButtonOnClickListener as OnClicksListener).onLongClick(this@AppDialog, BUTTON_POSITIVE)
                } else {
                    false
                }
            }
            else -> false
        }
    }

    private fun initialize(@StyleRes themeResId: Int) {
        val container = this.container ?: return
        val activity = this.activity ?: return

        var listItemDividerHeight = -1
        var listItemDivider: Drawable? = null
        var listItemSelector: Drawable? = null
        var listScrollbarFadingEnabled = true

        val attrs = activity.theme?.obtainStyledAttributes(themeResId, R.styleable.AppDialog)
        if (attrs != null) {
            // sets dialog attributes
            dialogWidth = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_width, 0)
            dialogWidthMargin = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_widthMargin, 0)
            dialogBackground = attrs.getDrawable(R.styleable.AppDialog_ad_background)
            dialogPaddingTop = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_paddingTop, 0)
            dialogPaddingBottom = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_paddingBottom, 0)
            dialogPaddingStart = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_paddingStart, 0)
            dialogPaddingEnd = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_paddingEnd, 0)

            // sets top attributes
            topBackground = attrs.getDrawable(R.styleable.AppDialog_ad_topBackground)
            topMinHeight = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_topMinHeight, 0)
            topPaddingTop = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_topPaddingTop, 0)
            topPaddingBottom = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_topPaddingBottom, 0)
            topPaddingStart = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_topPaddingStart, 0)
            topPaddingEnd = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_topPaddingEnd, 0)
            topDivider = attrs.getDrawable(R.styleable.AppDialog_ad_topDivider)
            topDividerHeight = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_topDividerHeight, 0)

            // sets title attributes
            titleTextColor = attrs.getColorStateList(R.styleable.AppDialog_ad_titleTextColor)
            titleTextSize = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_titleTextSize, 0)
            titleTextStyle = attrs.getInt(R.styleable.AppDialog_ad_titleTextStyle, 0)
            titleTextGravity = getGravityValue(attrs.getInt(R.styleable.AppDialog_ad_titleTextGravity, 0x30))
            titleTextLines = attrs.getInt(R.styleable.AppDialog_ad_titleTextLines, 1)
            titleTextEllipsize = getEllipsize(attrs.getInt(R.styleable.AppDialog_ad_titleTextEllipsize, -1))
            titlePaddingTop = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_titlePaddingTop, 0)
            titlePaddingBottom = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_titlePaddingBottom, 0)
            titlePaddingStart = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_titlePaddingStart, 0)
            titlePaddingEnd = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_titlePaddingEnd, 0)

            // sets contents attributes
            contentsBackground = attrs.getDrawable(R.styleable.AppDialog_ad_contentsBackground)
            contentsPaddingStart = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_contentsPaddingStart, 0)
            contentsPaddingTop = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_contentsPaddingTop, 0)
            contentsPaddingEnd = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_contentsPaddingEnd, 0)
            contentsPaddingBottom = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_contentsPaddingBottom, 0)
            contentsMinHeight = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_contentsMinHeight, 0)
            contentsMaxHeight = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_contentsMaxHeight, 0)

            // sets message attributes
            messageTextColor = attrs.getColorStateList(R.styleable.AppDialog_ad_messageTextColor)
            messageTextSize = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_messageTextSize, 0)
            messageTextLineSpacingExtra = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_messageTextLineSpacingExtra, 0).toFloat()
            messageTextGravity = getGravityValue(attrs.getInt(R.styleable.AppDialog_ad_messageTextGravity, 0x30))
            messageGapSize = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_messageGapSize, 0)

            // sets sub message attributes
            subMessageTextColor = attrs.getColorStateList(R.styleable.AppDialog_ad_subMessageTextColor)
            subMessageTextSize = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_subMessageTextSize, 0)
            subMessageTextLineSpacingExtra = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_subMessageTextLineSpacingExtra, 0).toFloat()
            subMessageTextGravity = getGravityValue(attrs.getInt(R.styleable.AppDialog_ad_subMessageTextGravity, 0x30))

            // sets bottom attributes
            bottomBackground = attrs.getDrawable(R.styleable.AppDialog_ad_bottomBackground)
            bottomMinHeight = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_bottomMinHeight, 0)
            bottomPaddingStart = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_bottomPaddingStart, 0)
            bottomPaddingTop = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_bottomPaddingTop, 0)
            bottomPaddingEnd = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_bottomPaddingEnd, 0)
            bottomPaddingBottom = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_bottomPaddingBottom, 0)
            bottomDivider = attrs.getDrawable(R.styleable.AppDialog_ad_bottomDivider)
            bottomDividerHeight = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_bottomDividerHeight, 0)

            // sets buttons
            buttonHeight = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_buttonHeight, 0)
            val buttonWidthDimension = attrs.getLayoutDimension(R.styleable.AppDialog_ad_buttonWidth, 0)
            buttonWidth = if (ViewGroup.LayoutParams.WRAP_CONTENT == buttonWidthDimension
                || ViewGroup.LayoutParams.MATCH_PARENT == buttonWidthDimension) {
                buttonWidthDimension
            } else {
                buttonWidthDimension.toFloat().roundToInt()
            }
            buttonWeight = attrs.getFloat(R.styleable.AppDialog_ad_buttonWeight, 0f)
            buttonMinWidth = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_buttonMinWidth, 0)
            buttonLayoutGravity = getGravityValue(attrs.getInt(R.styleable.AppDialog_ad_buttonLayoutGravity, 0x30))
            buttonTextSize = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_buttonTextSize, 0)
            buttonTextColor = attrs.getColorStateList(R.styleable.AppDialog_ad_buttonTextColor)
            buttonTextStyle = attrs.getInt(R.styleable.AppDialog_ad_buttonTextStyle, 0)
            buttonTextGravity = getGravityValue(attrs.getInt(R.styleable.AppDialog_ad_buttonTextGravity, 0x30))
            buttonMargin = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_buttonMargin, 0)
            buttonDivider = attrs.getDrawable(R.styleable.AppDialog_ad_buttonDivider)
            buttonDividerWidth = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_buttonDividerWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            buttonBackground = attrs.getDrawable(R.styleable.AppDialog_ad_buttonBackground)
            buttonSelectableBackground = attrs.getDrawable(R.styleable.AppDialog_ad_buttonSelectableBackground)
            leftButtonTextSize = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_leftButtonTextSize, 0)
            if (leftButtonTextSize == 0) {
                leftButtonTextSize = buttonTextSize
            }
            leftButtonTextColor = attrs.getColorStateList(R.styleable.AppDialog_ad_leftButtonTextColor)
            if (leftButtonTextColor == null) {
                leftButtonTextColor = buttonTextColor
            }
            rightButtonTextSize = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_rightButtonTextSize, 0)
            if (rightButtonTextSize == 0) {
                rightButtonTextSize = buttonTextSize
            }
            rightButtonTextColor = attrs.getColorStateList(R.styleable.AppDialog_ad_rightButtonTextColor)
            if (rightButtonTextColor == null) {
                rightButtonTextColor = buttonTextColor
            }

            // set left button bg drawable
            leftButtonBackground = attrs.getDrawable(R.styleable.AppDialog_ad_leftButtonBackground) ?: buttonBackground
            // set left button bg drawable in 3 buttons
            leftButtonIn3ButtonsBackground = attrs.getDrawable(R.styleable.AppDialog_ad_leftButtonIn3ButtonsBackground)
            if (leftButtonIn3ButtonsBackground == null) {
                leftButtonIn3ButtonsBackground = leftButtonBackground
            }
            leftButtonSelectableBackground = attrs.getDrawable(R.styleable.AppDialog_ad_leftButtonSelectableBackground) ?: buttonSelectableBackground

            // set center button bg drawable
            centerButtonBackground = attrs.getDrawable(R.styleable.AppDialog_ad_centerButtonBackground) ?: buttonBackground
            centerButtonSelectableBackground = attrs.getDrawable(R.styleable.AppDialog_ad_centerButtonSelectableBackground) ?: buttonSelectableBackground

            // set right button bg drawable
            rightButtonBackground = attrs.getDrawable(R.styleable.AppDialog_ad_rightButtonBackground) ?: buttonBackground
            // set right button bg drawable in 3 buttons
            rightButtonIn3ButtonsBackground = attrs.getDrawable(R.styleable.AppDialog_ad_rightButtonIn3ButtonsBackground)
            if (rightButtonIn3ButtonsBackground == null) {
                rightButtonIn3ButtonsBackground = rightButtonBackground
            }
            rightButtonSelectableBackground = attrs.getDrawable(R.styleable.AppDialog_ad_rightButtonSelectableBackground) ?: buttonSelectableBackground

            // set list layouts
            listItemLayout = attrs.getResourceId(R.styleable.AppDialog_ad_listItemLayout, listItemLayout)
            // set single choice item layout
            singleChoiceItemLayout = attrs.getResourceId(R.styleable.AppDialog_ad_listSingleChoiceItemLayout, singleChoiceItemLayout)
            // set list item divider
            listItemDivider = attrs.getDrawable(R.styleable.AppDialog_ad_listItemDivider)
            // set list item divider height
            listItemDividerHeight = attrs.getDimensionPixelSize(R.styleable.AppDialog_ad_listItemDividerHeight, 0)
            // set list item selector
            listItemSelector = attrs.getDrawable(R.styleable.AppDialog_ad_listItemSelector)
            // set list scrollbar fading enabled
            listScrollbarFadingEnabled = attrs.getBoolean(R.styleable.AppDialog_ad_listItemScrollbarFadingEnabled, true)

            attrs.recycle()
        }

        // set dialog background
        dialogBackground?.run {
            container.background = this
        }

        topContainer = container.findViewById(R.id.app_dialog_top_container)
        topContainer?.run {
            if (0 < topMinHeight) {
                this.minimumHeight = topMinHeight
            }
            this.isVisible = false
            this.background = topBackground
            this.setPadding(topPaddingStart, topPaddingTop, topPaddingEnd, topPaddingBottom)
        }

        iconImageView = container.findViewById(R.id.app_dialog_icon)
        iconImageView?.run {
            this.isVisible = false
        }

        titleContainer = container.findViewById(R.id.app_dialog_title_container)
        titleContainer?.run {
            this.isVisible = false
        }

        titleTextView = container.findViewById(R.id.app_dialog_title)
        titleTextView?.run {
            if (0 < topMinHeight) {
                this.setViewHeight(topMinHeight)
            }
            if (titleTextSize > 0) {
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize.toFloat())
            }
            if (titleTextStyle > 0) {
                var textStyle = Typeface.DEFAULT
                when (titleTextStyle) {
                    1 -> textStyle = Typeface.DEFAULT_BOLD
                }
                this.typeface = textStyle
            }
            if (titleTextColor != null) {
                this.setTextColor(titleTextColor)
            }
            if (0 < titleTextGravity) {
                this.gravity = titleTextGravity
            }
            this.setLines(titleTextLines)
            this.ellipsize = titleTextEllipsize
            this.setPadding(titlePaddingStart, titlePaddingTop, titlePaddingEnd, titlePaddingBottom)
        }

        topSeparatorView = container.findViewById(R.id.app_dialog_top_separator)
        topSeparatorView?.run {
            if (topDivider != null) {
                this.background = topDivider
            }
            if (topDividerHeight >= 0) {
                this.setViewHeight(topDividerHeight)
                this.isVisible = true
            }
        }

        contentsContainer = container.findViewById(R.id.app_dialog_contents_container)
        contentsContainer?.run {
            this.background = contentsBackground
            if (contentsMaxHeight > 0) {
                this.setMaximumHeight(contentsMaxHeight)
            }
            if (contentsMinHeight > 0) {
                this.minimumHeight = contentsMinHeight
            }
            this.setPadding(contentsPaddingStart, contentsPaddingTop, contentsPaddingEnd, contentsPaddingBottom)

            messageContainer = container.findViewById(R.id.app_dialog_contents_message_container)
            messageContainer?.run {
                messageTextView = container.findViewById(R.id.app_dialog_contents_message)
                messageTextView?.run {
                    if (messageTextColor != null) {
                        this.setTextColor(messageTextColor)
                    }
                    this.gravity = messageTextGravity
                    if (messageTextSize > 0) {
                        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageTextSize.toFloat())
                    }
                    if (messageTextLineSpacingExtra >= 0) {
                        this.setLineSpacing(messageTextLineSpacingExtra, 1.0f)
                    }
                }
                messageGapView = container.findViewById(R.id.app_dialog_contents_message_gap)
                messageGapView?.run {
                    if (0 < messageGapSize) {
                        this.setViewHeight(messageGapSize)
                    }
                }

                subMessageTextView = container.findViewById(R.id.app_dialog_contents_sub_message)
                subMessageTextView?.run {
                    if (subMessageTextColor != null) {
                        this.setTextColor(subMessageTextColor)
                    }
                    this.gravity = subMessageTextGravity
                    if (subMessageTextSize > 0) {
                        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, subMessageTextSize.toFloat())
                    }
                    if (subMessageTextLineSpacingExtra >= 0) {
                        this.setLineSpacing(subMessageTextLineSpacingExtra, 1.0f)
                    }
                }
            }

            listView = container.findViewById(R.id.app_dialog_contents_list)
            listView?.run {
                if (listItemDivider != null) {
                    this.divider = listItemDivider
                }
                if (listItemDividerHeight >= 0) {
                    this.dividerHeight = listItemDividerHeight
                }
                if (listItemSelector != null) {
                    this.selector = listItemSelector
                }
                this.isScrollbarFadingEnabled = listScrollbarFadingEnabled
            }
        }

        bottomContainer = container.findViewById(R.id.app_dialog_bottom_container)
        bottomContainer?.run {
            this.background = bottomBackground
            this.setPadding(bottomPaddingStart, bottomPaddingTop, bottomPaddingEnd, bottomPaddingBottom)
            if (bottomMinHeight > 0) {
                this.minimumHeight = bottomMinHeight
            } else {
                this.isVisible = false
            }

            buttonContainer = container.findViewById(R.id.app_dialog_button_container)
            buttonContainer?.run {
                this.gravity = buttonLayoutGravity
                if (buttonHeight > 0) {
                    this.setViewHeight(buttonHeight)
                }

                var textStyle = Typeface.DEFAULT
                when (buttonTextStyle) {
                    1 -> textStyle = Typeface.DEFAULT_BOLD
                }

                negativeButtonContainer = container.findViewById(R.id.app_dialog_button_left_container)
                negativeButtonContainer?.run {
                    this.setViewWidth(buttonWidth)
                    this.setViewMargin(buttonMargin)
                    if (0 == buttonWidth) {
                        this.setViewWeight(buttonWeight)
                    }
                    this.setOnClickListener(buttonOnClickListener)
                    this.clipToOutline = true

                    negativeButton = container.findViewById(R.id.app_dialog_button_left)
                    negativeButton?.run {
                        this.minimumWidth = buttonMinWidth
                        if (leftButtonTextColor != null) {
                            this.setTextColor(leftButtonTextColor)
                        }
                        if (leftButtonTextSize > 0) {
                            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftButtonTextSize.toFloat())
                        }
                        this.typeface = textStyle
                        this.setLayoutGravity(buttonTextGravity)
                    }
                }

                buttonLeftSeparatorView = container.findViewById(R.id.app_dialog_button_left_separator)
                buttonLeftSeparatorView?.run {
                    this.background = buttonDivider
                    if (buttonDividerWidth >= 0) {
                        this.setViewWidth(buttonDividerWidth)
                    }
                }

                neutraButtonContainer = container.findViewById(R.id.app_dialog_button_center_container)
                neutraButtonContainer?.run {
                    this.setViewWidth(buttonWidth)
                    this.setViewMargin(buttonMargin)
                    if (0 == buttonWidth) {
                        this.setViewWeight(buttonWeight)
                    }
                    this.setOnClickListener(buttonOnClickListener)
                    this.clipToOutline = true

                    neutraButton = container.findViewById(R.id.app_dialog_button_center)
                    neutraButton?.run {
                        this.minimumWidth = buttonMinWidth
                        if (buttonTextColor != null) {
                            this.setTextColor(buttonTextColor)
                        }
                        if (buttonTextSize > 0) {
                            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize.toFloat())
                        }
                        this.typeface = textStyle
                        this.setLayoutGravity(buttonTextGravity)
                    }
                }

                buttonRightSeparatorView = container.findViewById(R.id.app_dialog_button_right_separator)
                buttonRightSeparatorView?.run {
                    this.background = buttonDivider
                    if (buttonDividerWidth >= 0) {
                        this.setViewWidth(buttonDividerWidth)
                    }
                }

                positiveButtonContainer = container.findViewById(R.id.app_dialog_button_right_container)
                positiveButtonContainer?.run {
                    this.setViewWidth(buttonWidth)
                    this.setViewMargin(buttonMargin)
                    if (0 == buttonWidth) {
                        this.setViewWeight(buttonWeight)
                    }
                    this.setOnClickListener(buttonOnClickListener)
                    this.clipToOutline = true

                    positiveButton = container.findViewById(R.id.app_dialog_button_right)
                    positiveButton?.run {
                        this.minimumWidth = buttonMinWidth
                        if (rightButtonTextColor != null) {
                            this.setTextColor(rightButtonTextColor)
                        }
                        if (rightButtonTextSize > 0) {
                            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightButtonTextSize.toFloat())
                        }
                        this.typeface = textStyle
                        this.setLayoutGravity(buttonTextGravity)
                    }
                }
            }

            bottomSeparatorView = container.findViewById(R.id.app_dialog_bottom_separator)
            bottomSeparatorView?.run {
                if (bottomDivider != null) {
                    this.background = bottomDivider
                }
                if (bottomDividerHeight >= 0) {
                    this.setViewHeight(bottomDividerHeight)
                    this.isVisible = true
                }
            }
        }
    }

    /**
     * Set padding of dialog
     */
    fun setPadding(padding: Int) {
        container?.setViewPadding(padding)
    }

    /**
     * Set padding of dialog
     */
    fun setPadding(start: Int, top: Int, end: Int, bottom: Int) {
        container?.setPadding(start, top, end, bottom)
    }

    /**
     * Set margins of dialog's window.
     * @param margins margin value (start, top, end, bottom)
     */
    fun setMargins(margins: Int) {
        container?.setViewMargin(margins)
    }

    /**
     * Set dialog margin
     */
    fun setMargins(start: Int, top: Int, end: Int, bottom: Int) {
        container?.setViewMargin(start, top, end, bottom)
    }

    /**
     * Set the title text for this dialog's window. The text is retrieved
     * from the resources with the supplied identifier.
     *
     * @param titleId the title's text resource identifier
     */
    fun setTitle(titleId: Int) {
        titleTextView?.setText(titleId)
        titleContainer?.isVisible = true
        topContainer?.isVisible = true
    }

    /**
     * Set the title text for this dialog's window.
     *
     * @param title The new text to display in the title.
     */
    fun setTitle(title: CharSequence?) {
        titleTextView?.text = title
        titleContainer?.isVisible = true
        topContainer?.isVisible = true
    }

    fun setIcon(@DrawableRes resID: Int) {
        this.iconImageView?.run {
            if (resID != 0) {
                this.setImageResource(resID)
                this.isVisible = true
            } else {
                this.isVisible = false
            }
        }
    }

    fun setIcon(drawable: Drawable?) {
        iconImageView?.setImageDrawable(drawable)
        iconImageView?.isVisible = drawable != null
    }

    fun setMessage(messageId: Int) {
        setMessage(messageId, mutableListOf())
    }

    fun setMessage(messageId: Int, links: MutableList<TextLinkAttribute> = mutableListOf()) {
        listView?.isVisible = false

        messageContainer?.let {
            it.isVisible = true
            messageTextView?.run {
                this.isVisible = true
                setText(messageId)

                for (link in links) {
                    this.addLink(link.target, link.url)
                }
            }
        }

        updateMessageGap()
    }

    fun setMessage(message: CharSequence?) {
        setMessage(message, mutableListOf())
    }

    fun setMessage(message: CharSequence?, links: MutableList<TextLinkAttribute> = mutableListOf()) {
        listView?.isVisible = false

        messageContainer?.let {
            it.isVisible = true
            messageTextView?.run {
                this.isVisible = true
                text = message

                for (link in links) {
                    this.addLink(link.target, link.url)
                }
            }
        }

        updateMessageGap()
    }

    fun setSubMessage(messageId: Int) {
        listView?.isVisible = false

        messageContainer?.let {
            it.isVisible = true
            subMessageTextView?.run {
                this.isVisible = true
                setText(messageId)
            }
        }

        updateMessageGap()
    }

    fun setSubMessage(message: CharSequence?) {
        listView?.isVisible = false

        messageContainer?.let {
            it.isVisible = true
            subMessageTextView?.run {
                this.isVisible = true
                text = message
            }
        }

        updateMessageGap()
    }

    private fun updateMessageGap() {
        messageGapView?.isVisible = (messageTextView?.isVisible == true) && (subMessageTextView?.isVisible == true)
    }

    fun setPositiveButton(@StringRes textId: Int, @ColorInt color: Int, listener: DialogInterface.OnClickListener? = null) {
        setPositiveButton(textId, ColorStateList.valueOf(color), listener)
    }

    fun setPositiveButton(@StringRes textId: Int, color: ColorStateList? = null, listener: DialogInterface.OnClickListener? = null) {
        positiveButton?.run {
            this.setText(textId)
            if (color != null) {
                this.setTextColor(color)
            }
        }

        showPositiveButton(listener)
    }

    fun setPositiveButton(text: CharSequence?, @ColorInt color: Int, listener: DialogInterface.OnClickListener? = null) {
        setPositiveButton(text, ColorStateList.valueOf(color), listener)
    }

    fun setPositiveButton(text: CharSequence?, color: ColorStateList? = null, listener: DialogInterface.OnClickListener? = null) {
        positiveButton?.run {
            this.text = text
            if (color != null) {
                this.setTextColor(color)
            }
        }
        showPositiveButton(listener)
    }

    private fun showPositiveButton(listener: DialogInterface.OnClickListener? = null) {
        bottomContainer?.isVisible = true

        positiveButtonContainer?.run {
            this.isVisible = true
            this.setOnClickListener(buttonOnClickListener)
            if (listener is OnClicksListener) {
                this.setOnLongClickListener(buttonOnLongClickListener)
            }
        }
        positiveButtonOnClickListener = listener ?: nullOnClickListener
        setButtonStyles()
    }

    fun setNegativeButton(@StringRes textId: Int, @ColorInt color: Int, listener: DialogInterface.OnClickListener? = null) {
        setNegativeButton(textId, ColorStateList.valueOf(color), listener)
    }

    fun setNegativeButton(@StringRes textId: Int, color: ColorStateList? = null, listener: DialogInterface.OnClickListener? = null) {
        negativeButton?.run {
            this.setText(textId)
            if (color != null) {
                this.setTextColor(color)
            }
        }

        showNegativeButton(listener)
    }

    fun setNegativeButton(text: CharSequence?, @ColorInt color: Int, listener: DialogInterface.OnClickListener? = null) {
        setNegativeButton(text, ColorStateList.valueOf(color), listener)
    }

    fun setNegativeButton(text: CharSequence?, color: ColorStateList? = null, listener: DialogInterface.OnClickListener? = null) {
        negativeButton?.run {
            this.text = text
            if (color != null) {
                this.setTextColor(color)
            }
        }

        showNegativeButton(listener)
    }

    private fun showNegativeButton(listener: DialogInterface.OnClickListener? = null) {
        bottomContainer?.isVisible = true

        negativeButtonContainer?.run {
            this.isVisible = true
            this.setOnClickListener(buttonOnClickListener)
            if (listener is OnClicksListener) {
                this.setOnLongClickListener(buttonOnLongClickListener)
            }
        }

        negativeButtonOnClickListener = listener ?: nullOnClickListener

        setButtonStyles()
    }

    fun setNeutralButton(@StringRes textId: Int, @ColorInt color: Int, listener: DialogInterface.OnClickListener? = null) {
        setNeutralButton(textId, ColorStateList.valueOf(color), listener)
    }

    fun setNeutralButton(@StringRes textId: Int, color: ColorStateList? = null, listener: DialogInterface.OnClickListener? = null) {
        neutraButton?.run {
            this.setText(textId)
            if (color != null) {
                this.setTextColor(color)
            }
        }

        showNeutralButton(listener)
    }

    fun setNeutralButton(text: CharSequence?, @ColorInt color: Int, listener: DialogInterface.OnClickListener? = null) {
        setNeutralButton(text, ColorStateList.valueOf(color), listener)
    }

    fun setNeutralButton(text: CharSequence?, color: ColorStateList? = null, listener: DialogInterface.OnClickListener? = null) {
        neutraButton?.run {
            this.text = text
            if (color != null) {
                this.setTextColor(color)
            }
        }

        showNeutralButton(listener)
    }

    private fun showNeutralButton(listener: DialogInterface.OnClickListener? = null) {
        bottomContainer?.isVisible = true

        neutraButtonContainer?.run {
            this.isVisible = true
            this.setOnClickListener(buttonOnClickListener)
            if (listener is OnClicksListener) {
                this.setOnLongClickListener(buttonOnLongClickListener)
            }
        }

        neutraButtonOnClickListener = listener ?: nullOnClickListener

        setButtonStyles()
    }

    fun setCancelable(cancelable: Boolean) {
        dialog?.setCancelable(cancelable)
    }

    fun setCanceledOnTouchOutside(cancel: Boolean) {
        dialog?.setCanceledOnTouchOutside(cancel)
    }

    fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?) {
        this.onCancelListener = onCancelListener
        dialog?.setOnCancelListener(this.onCancelListener)
    }

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?) {
        this.onDismissListener = onDismissListener
        dialog?.setOnDismissListener(this.onDismissListener)
    }

    fun setOnShowListener(onShowListener: DialogInterface.OnShowListener?) {
        this.onShowListener = onShowListener
        dialog?.setOnShowListener(this.onShowListener)
    }

    fun setAnimationStyle(@StyleRes style: Int) {
        val dialog = this.dialog ?: return
        val window = dialog.window
        window?.attributes?.windowAnimations = style
    }

    /**
     * Add custom view to dialog. If custom view is not null, previous child views of dialog will be removed from dialog.
     * @param view new custom view
     */
    fun setCustomView(view: View?) {
        view ?: return

        container?.run {
            this.removeAllViews()
            this.addView(view)
        }
    }

    /**
     * Set the view to display in the dialog.
     */
    fun setView(view: View?) {
        view ?: return

        contentsContainer?.run {
            this.setViewHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            this.setViewPadding(0)
            this.minimumHeight = 0

            this.removeAllViews()
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.CENTER
            view.layoutParams = params
            this.addView(view)
        }
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of
     * the selected item via the supplied listener. This should be an array type i.e.
     * R.array.foo The list will have a check mark displayed to the right of the text for the
     * checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a
     * button will dismiss the dialog.
     *
     * @param itemsId the resource id of an array i.e. R.array.foo
     * @param checkedItem specifies which item is checked. If -1 no items are checked.
     * @param listener notified when an item on the list is clicked. The dialog will not be
     * dismissed when an item is clicked. It will only be dismissed if clicked on a
     * button, if no buttons are supplied it's up to the user to dismiss the dialog.
     */
    fun setSingleChoiceItems(@ArrayRes itemsId: Int, checkedItem: Int, listener: DialogInterface.OnClickListener? = null) {
        setSingleChoiceItems(context?.resources?.getStringArray(itemsId), checkedItem, listener)
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of
     * the selected item via the supplied listener. The list will have a check mark displayed to
     * the right of the text for the checked item. Clicking on an item in the list will not
     * dismiss the dialog. Clicking on a button will dismiss the dialog.
     *
     * @param items the items to be displayed.
     * @param checkedItem specifies which item is checked. If -1 no items are checked.
     * @param listener notified when an item on the list is clicked. The dialog will not be
     * dismissed when an item is clicked. It will only be dismissed if clicked on a
     * button, if no buttons are supplied it's up to the user to dismiss the dialog.
     */
    fun setSingleChoiceItems(items: Array<String>?, checkedItem: Int, listener: DialogInterface.OnClickListener? = null) {
        messageContainer?.isVisible = false

        listView?.run {
            this.isVisible = true
            this.choiceMode = ListView.CHOICE_MODE_SINGLE
            val adapter: ListAdapter = CheckedItemAdapter(context, singleChoiceItemLayout, android.R.id.text1, items ?: arrayOf())
            this.adapter = adapter
            if (checkedItem >= 0) {
                this.setSelection(checkedItem)
                this.setItemChecked(checkedItem, true)
            }
            if (listener != null) {
                this.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> listener.onClick(this@AppDialog, position) }
            }
        }
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the
     * selected item via the supplied listener. This should be an array type i.e. R.array.foo
     *
     */
    fun setListItems(items: Array<String>?, listener: DialogInterface.OnClickListener? = null) {
        val context = this.context ?: return

        messageContainer?.isVisible = false

        listView?.run {
            this.isVisible = true
            this.choiceMode = ListView.CHOICE_MODE_SINGLE
            this.adapter = CheckedItemAdapter(context, listItemLayout, android.R.id.text1, items ?: arrayOf())
            if (listener != null) {
                this.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> listener.onClick(this@AppDialog, position) }
            }
        }
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the
     * selected item via the supplied listener. This should be an array type i.e. R.array.foo
     *
     */
    fun setListItems(@ArrayRes itemsId: Int, listener: DialogInterface.OnClickListener? = null) {
        setListItems(context?.resources?.getStringArray(itemsId), listener)
    }

    fun setAdapter(adapter: ListAdapter?, listener: DialogInterface.OnClickListener? = null) {
        messageContainer?.isVisible = false

        listView?.run {
            this.isVisible = true
            this.choiceMode = ListView.CHOICE_MODE_NONE
            if (adapter != null) {
                this.adapter = adapter
            }
            if (listener != null) {
                this.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> listener.onClick(this@AppDialog, position) }
            }
        }
    }

    fun setFadeScrollbars(fade: Boolean) {
        listView?.isScrollbarFadingEnabled = fade
    }

    fun show() {
        dialog?.show()
    }

    override fun cancel() {
        dialog?.cancel()
    }

    override fun dismiss() {
        dialog?.dismiss()
    }

    private fun setButtonStyles() {
        var leftButtonVisible = false
        var centerButtonVisible = false
        var rightButtonVisible = false

        if (negativeButtonContainer?.isVisible == true) {
            leftButtonVisible = true
        }

        if (neutraButtonContainer?.isVisible == true) {
            centerButtonVisible = true
        }

        if (positiveButtonContainer?.isVisible == true) {
            rightButtonVisible = true
        }

        var showLeftSeparator = false
        var showRightSeparator = false

        if (leftButtonVisible && centerButtonVisible && rightButtonVisible) {
            showLeftSeparator = true
            showRightSeparator = true

            rightButtonIn3ButtonsBackground?.let { drawable ->
                positiveButtonContainer?.background = drawable
            }
            rightButtonSelectableBackground?.let { drawable ->
                positiveButtonContainer?.foreground = drawable
            }

            centerButtonBackground?.let { drawable ->
                neutraButtonContainer?.background = drawable
            }
            centerButtonSelectableBackground?.let { drawable ->
                neutraButtonContainer?.foreground = drawable
            }

            leftButtonIn3ButtonsBackground?.let { drawable ->
                negativeButtonContainer?.background = drawable
            }
            leftButtonSelectableBackground?.let { drawable ->
                negativeButtonContainer?.foreground = drawable
            }
        } else if (leftButtonVisible && (centerButtonVisible || rightButtonVisible)) {
            showLeftSeparator = true

            if (rightButtonVisible) {
                rightButtonBackground?.let { drawable ->
                    positiveButtonContainer?.background = drawable
                }
                rightButtonSelectableBackground?.let { drawable ->
                    positiveButtonContainer?.foreground = drawable
                }
            }
            if (centerButtonVisible) {
                centerButtonBackground?.let { drawable ->
                    neutraButtonContainer?.background = drawable
                }
                centerButtonSelectableBackground?.let { drawable ->
                    neutraButtonContainer?.foreground = drawable
                }
            }
            if (leftButtonVisible) {
                leftButtonBackground?.let { drawable ->
                    negativeButtonContainer?.background = drawable
                }
                leftButtonSelectableBackground?.let { drawable ->
                    negativeButtonContainer?.foreground = drawable
                }
            }
        } else if (centerButtonVisible && rightButtonVisible) {
            showRightSeparator = true

            rightButtonBackground?.let { drawable ->
                positiveButtonContainer?.background = drawable
            }
            rightButtonSelectableBackground?.let { drawable ->
                positiveButtonContainer?.foreground = drawable
            }

            leftButtonBackground?.let { drawable ->
                neutraButtonContainer?.background = drawable
            }
            leftButtonSelectableBackground?.let { drawable ->
                neutraButtonContainer?.foreground = drawable
            }
        } else {
            buttonBackground?.let { drawable ->
                if (rightButtonVisible) {
                    positiveButtonContainer?.background = drawable
                }
                if (leftButtonVisible) {
                    negativeButtonContainer?.background = drawable
                }
                if (centerButtonVisible) {
                    neutraButtonContainer?.background = drawable
                }
            }

            buttonSelectableBackground?.let { drawable ->
                if (rightButtonVisible) {
                    positiveButtonContainer?.foreground = drawable
                }
                if (leftButtonVisible) {
                    negativeButtonContainer?.foreground = drawable
                }
                if (centerButtonVisible) {
                    neutraButtonContainer?.foreground = drawable
                }
            }
        }

        if (showLeftSeparator) {
            buttonLeftSeparatorView?.isVisible = true
        }

        if (showRightSeparator) {
            buttonRightSeparatorView?.isVisible = true
        }
    }

    fun isShowing(): Boolean = dialog?.isShowing == true

    fun setBackgroundDrawable(drawable: Drawable?) {
        // set dialog background
        dialogBackground = drawable
        container?.background = dialogBackground
    }

    fun getButton(button: Int): TextView? {
        when (button) {
            BUTTON_POSITIVE -> return positiveButton
            BUTTON_NEGATIVE -> return negativeButton
            BUTTON_NEUTRAL -> return neutraButton
        }
        return null
    }

    fun setDialogDimAmount(dimAmount: Float) {
        dialog?.window?.setDimAmount(dimAmount)
    }

    fun setDialogBackgroundDrawable(drawable: Drawable?) {
        setDialogBackgroundDrawable(drawable, -1f)
    }

    fun setDialogBackgroundDrawable(drawable: Drawable?, dimAmount: Float) {
        val window = dialog?.window ?: return
        window.setBackgroundDrawable(drawable)
        if (dimAmount >= 0) {
            window.setDimAmount(dimAmount)
        }
    }

    fun setDialogSize(width: Int, height: Int) {
        val window = this.dialog?.window ?: return
        if (window.attributes != null) {
            setDialogSize(width, height, window.attributes.gravity)
        }
    }

    fun setDialogSize(width: Int, height: Int, gravity: Int) {
        val window = this.dialog?.window ?: return
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window.attributes)
        container?.run {
            val layoutParams = this.layoutParams
            if (layoutParams != null) {
                layoutParams.width = width
                layoutParams.height = height
            }
            this.layoutParams = layoutParams
        }

        // This makes the dialog take up the full width
        lp.width = width
        lp.height = height
        lp.gravity = gravity
        window.attributes = lp
    }

    fun setDialogLocation(xPos: Int, yPos: Int) {
        val window = this.dialog?.window ?: return
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window.attributes)

        // set dialog position
        lp.gravity = Gravity.TOP or Gravity.START
        lp.x = xPos //x position
        lp.y = yPos // y position
        window.attributes = lp
    }

    private class CheckedItemAdapter(
        context: Context, resource: Int, textViewResourceId: Int,
        objects: Array<String>
    ) : ArrayAdapter<String>(context, resource, textViewResourceId, objects) {
        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }

    /**
     * name="top" value="0x01"
     * name="bottom" value="0x02"
     * name="start" value="0x04"
     * name="end" value="0x08"
     * name="center_vertical" value="0x10"
     * name="center_horizontal" value="0x20"
     * name="center" value="0x30"
     */
    private fun getGravityValue(attrsValue: Int): Int {
        var gravity = 0
        if (attrsValue and 0x30 == 0x30) {
            gravity = Gravity.CENTER
            return gravity
        }
        if (attrsValue and 0x01 == 0x01) { // top
            gravity = gravity or Gravity.TOP
        }
        if (attrsValue and 0x02 == 0x02) { // bottom
            gravity = gravity or Gravity.BOTTOM
        }
        if (attrsValue and 0x04 == 0x04) { // start
            gravity = gravity or Gravity.START
        }
        if (attrsValue and 0x08 == 0x08) { // end
            gravity = gravity or Gravity.END
        }
        if (attrsValue and 0x10 == 0x10) { // center vertical
            gravity = gravity or Gravity.CENTER_VERTICAL
        }
        if (attrsValue and 0x20 == 0x20) { // center horizontal
            gravity = gravity or Gravity.CENTER_HORIZONTAL
        }
        return gravity
    }

    /**
     *  ELLIPSIZE_NOT_SET = -1;
     *  ELLIPSIZE_NONE = 0;
     *  ELLIPSIZE_START = 1;
     *  ELLIPSIZE_MIDDLE = 2;
     *  ELLIPSIZE_END = 3;
     *  ELLIPSIZE_MARQUEE = 4;
     */
    private fun getEllipsize(ellipsize: Int): TextUtils.TruncateAt? {
        return when (ellipsize) {
            1 -> // ELLIPSIZE_START
                TextUtils.TruncateAt.START
            2 -> // ELLIPSIZE_MIDDLE
                TextUtils.TruncateAt.MIDDLE
            3 -> //ELLIPSIZE_END
                TextUtils.TruncateAt.END
            4 -> // ELLIPSIZE_MARQUEE
                TextUtils.TruncateAt.MARQUEE
            else -> // ELLIPSIZE_NOT_SET
                null
        }
    }

    companion object {
        const val MATCH_PARENT = WindowManager.LayoutParams.MATCH_PARENT
        const val WRAP_CONTENT = WindowManager.LayoutParams.WRAP_CONTENT
        const val BUTTON_POSITIVE = DialogInterface.BUTTON_POSITIVE

        /**
         * The identifier for the negative button.
         */
        const val BUTTON_NEGATIVE = DialogInterface.BUTTON_NEGATIVE

        /**
         * The identifier for the neutral button.
         */
        const val BUTTON_NEUTRAL = DialogInterface.BUTTON_NEUTRAL
        private const val ARG_STYLE = "style"
    }

    init {
        try {
            val _activity = activity ?: throw IllegalArgumentException()
            this.activity = _activity

            val _context = _activity.applicationContext ?: throw IllegalArgumentException()
            this.context = _context

            val inflater = this.activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            container = inflater?.inflate(R.layout.app_dialog, FrameLayout(_context), false) as FrameLayout?
            container?.run {
                this.clipToOutline = true
            }

            initialize(themeResId)

            container?.let { container ->
                this.dialog = Dialog(_activity, dialogThemeResId).apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    val window = this.window
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setCancelable(true)
                    setContentView(container)

                    // set dialog size
                    if (null != window) {
                        if (0 < dialogWidthMargin) {
                            val wm = _activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
                            if (wm != null) {
                                val display = wm.defaultDisplay
                                val metrics = DisplayMetrics()
                                display.getMetrics(metrics)
                                val width = metrics.widthPixels
                                //                                val height = metrics.heightPixels
                                val lp = WindowManager.LayoutParams()
                                lp.copyFrom(window.attributes)
                                lp.width = width - 2 * dialogWidthMargin
                                window.attributes = lp
                            }
                        } else if (dialogWidth > 0) {
                            val lp = WindowManager.LayoutParams()
                            lp.copyFrom(window.attributes)
                            lp.width = dialogWidth
                            window.attributes = lp
                        }
                    }
                }
            }
        } catch (ex: IllegalArgumentException) {

        }
    }
}

data class TextLinkAttribute(val target: String, val url: String?)