package com.hane24.hoursarenotenough24.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.data.CalendarItem
import com.hane24.hoursarenotenough24.data.LogTableItem
import com.hane24.hoursarenotenough24.inoutlog.LogListFragment
import com.hane24.hoursarenotenough24.inoutlog.LogTableAdapter
import com.hane24.hoursarenotenough24.network.ReissueState

fun getColorHelper(context: Context, id: Int) =
    if (Build.VERSION.SDK_INT >= 23) context.getColor(id) else context.resources.getColor(id)

//@BindingAdapter("latestTime", "isStateOn", requireAll = false)
//fun latestTimeText(
//    view: TextView,
//    timeString: String,
//    state: Boolean
//) {
//    if (state) {
//        view.text = view.context.getString(R.string.latest_time_format, timeString)
//        view.visibility = View.VISIBLE
//    } else {
//        view.visibility = View.INVISIBLE
//    }
//}

@BindingAdapter(value = ["isStateOn"], requireAll = false)
fun ProgressBar.isStateOn(state: Boolean) {
    if (state) {
        progressTintList =
            ColorStateList.valueOf(getColorHelper(context, R.color.on_progress_front))
        progressBackgroundTintList =
            ColorStateList.valueOf(getColorHelper(context, R.color.on_progress_back))
    } else {
        progressTintList =
            ColorStateList.valueOf(getColorHelper(context, R.color.off_progress_front))
        progressBackgroundTintList =
            ColorStateList.valueOf(getColorHelper(context, R.color.off_progress_back))
    }
}

@BindingAdapter("logoColor")
fun bindLogoColor(
    view: ImageView,
    state: Boolean
) {
    view.imageTintList = if (state) {
        ColorStateList.valueOf(getColorHelper(view.context, R.color.on_icon_color))
    } else {
        ColorStateList.valueOf(getColorHelper(view.context, R.color.off_icon_color))
    }
}

@BindingAdapter("isEnabled")
fun bindRefreshClickable(
    view: ImageView,
    state: Boolean
) {
    view.isEnabled = state
}

@BindingAdapter("overViewLoading", "listViewLoading")
fun bindRefreshVisible(
    view: ImageView,
    overViewState: Boolean,
    listViewState: Boolean
) {
    view.visibility = if (overViewState || listViewState) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("overViewLoading", "listViewLoading")
fun bindProgressVisible(
    view: ProgressBar,
    overViewState: Boolean,
    listViewState: Boolean
) {
    view.visibility = if (overViewState || listViewState) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

@BindingAdapter("isEnabled")
fun bindDrawerClickable(
    view: ImageButton,
    state: Boolean
) {
    view.isEnabled = state
}


@BindingAdapter("leftBtnYear", "leftBtnMonth", "loadingState", requireAll = false)
fun bindLeftButtonState(
    button: ImageButton,
    year: Int,
    month: Int,
    state: Boolean
) {
    button.isEnabled = !state && !(year == 2022 && month == 8)
}

@BindingAdapter("rightBtnYear", "rightBtnMonth", "loadingState", requireAll = false)
fun bindRightButtonState(
    button: ImageButton,
    year: Int,
    month: Int,
    state: Boolean
) {
    button.isEnabled =
        !state && (year != TodayCalendarUtils.year || month != TodayCalendarUtils.month)
}

@BindingAdapter("tableList")
fun bindTableRecyclerView(
    recyclerView: RecyclerView,
    data: List<LogTableItem>?
) {
    recyclerView.visibility = if (data?.isEmpty() != false) View.INVISIBLE else View.VISIBLE
    val adapter = recyclerView.adapter as LogTableAdapter
    adapter.submitList(data?.reversed())
}

@BindingAdapter("tableList")
fun bindTableTextView(
    textView: TextView,
    data: List<LogTableItem>?
) {
    textView.visibility = if (data?.isEmpty() != false) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("item")
fun bindIsMissingItem(
    view: ConstraintLayout,
    item: LogTableItem,
) {
    if (item.durationTime == "누락") {
        view.background =
            ResourcesCompat.getDrawable(
                view.resources,
                R.drawable.missing_record_background,
                view.resources.newTheme()
            )
    } else {
        view.setBackgroundResource(0)
    }
}

@BindingAdapter("calendarGrid")
fun bindCalendarRecyclerView(
    recyclerView: RecyclerView,
    data: List<CalendarItem>?
) {
    val adapter = recyclerView.adapter as LogListFragment.LogCalendarAdapter
    adapter.submitList(data)
}

@BindingAdapter("item", "selectedDay", "selectedMonth", requireAll = false)
fun bindCalendarItem(
    view: MaterialButton,
    item: CalendarItem,
    selectedDay: Int,
    selectedMonth: Int,
) {
    view.text = when (item.day) {
        -7 -> "일"
        -6 -> "월"
        -5 -> "화"
        -4 -> "수"
        -3 -> "목"
        -2 -> "금"
        -1 -> "토"
        else -> item.day.toString()
    }
    if (item.day < 0) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        view.setTextColor(getColorHelper(view.context, R.color.calendar_week_of_day_text))
        view.isEnabled = false
        return
//        view.textSize = view.context.resources.getDimension(R.dimen.day_of_week_text_size)
    }

    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
    view.strokeWidth = 0

    if (selectedMonth == TodayCalendarUtils.month && item.day == TodayCalendarUtils.day && item.day != selectedDay) {
        view.setTextColor(getColorHelper(view.context, R.color.selected_background_color))
        view.backgroundTintList =
            ColorStateList.valueOf(getColorHelper(view.context, R.color.white))
        view.cornerRadius =
            view.context.resources.getDimensionPixelSize(R.dimen.calendar_common_radius)
        view.setTextAppearance(view.context, R.style.NormalTextStyle)
        view.strokeWidth = 2
    } else if (item.day == selectedDay) {
        view.setTextColor(getColorHelper(view.context, R.color.selected_text_color))
        view.backgroundTintList =
            ColorStateList.valueOf(getColorHelper(view.context, R.color.selected_background_color))
        view.cornerRadius =
            view.context.resources.getDimensionPixelSize(R.dimen.calendar_selected_radius)
        view.setTextAppearance(view.context, R.style.BoldTextStyle)
    } else {
        view.setTextColor(getColorHelper(view.context, R.color.black))
        view.backgroundTintList = ColorStateList.valueOf(getColorHelper(view.context, item.color))
        view.cornerRadius =
            view.context.resources.getDimensionPixelSize(R.dimen.calendar_common_radius)
        view.setTextAppearance(view.context, R.style.NormalTextStyle)
//        ColorStateList.valueOf(getColorHelper(view.context, R.color.calendar_item_stroke_default))
    }

    if (item.isNextDay) {
        view.setTextColor(getColorHelper(view.context, R.color.next_day_text))
        view.isEnabled = false
    } else {
//        view.setTextColor(getColorHelper(view.context, R.color.black))
        view.isEnabled = true
    }
}

@BindingAdapter("loadingState")
fun setCalendarVisible(
    recyclerView: RecyclerView,
    loadingState: Boolean
) {
    recyclerView.visibility = if (loadingState) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("loadingState")
fun setProgressBarVisible(
    view: ImageView,
    loadingState: Boolean
) {
    val drawable = view.drawable as AnimationDrawable
    if (loadingState) {
        drawable.setEnterFadeDuration(300)
        drawable.setExitFadeDuration(300)
        drawable.start()
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.INVISIBLE
        drawable.stop()
    }
}

@BindingAdapter("loadImage")
fun loadImage(
    view: ImageView,
    imageUrl: String,
) {
    Glide.with(view.context).load(imageUrl).into(view)
}

@BindingAdapter("setBackground")
fun setBackground(
    view: ViewGroup,
    flag: Boolean
) {
    view.background = if (flag) {
        AppCompatResources.getDrawable(view.context, R.drawable.in_background)
    } else {
        AppCompatResources.getDrawable(view.context, R.color.overview_in_color)
    }
}


@BindingAdapter("serverState")
fun reissueImageViewStateCheck(
    view: ImageView,
    state: ReissueState?,
) {
    state?.let {
        view.background = when (view.id) {
            R.id.reissue_state_apply_image -> {
                if (it.state == "apply") {
                    AppCompatResources.getDrawable(view.context, R.drawable.ic_reissue_apply_yes)
                } else {
                    AppCompatResources.getDrawable(view.context, R.drawable.ic_reissue_apply_no)
                }
            }
            R.id.reissue_state_make_image -> {
                if (it.state == "in_progress") {
                    AppCompatResources.getDrawable(view.context, R.drawable.ic_reissue_make_yes)
                } else {
                    AppCompatResources.getDrawable(view.context, R.drawable.ic_reissue_make_no)
                }
            }
            else -> {
                if (it.state == "pick_up_requested") {
                    AppCompatResources.getDrawable(view.context, R.drawable.ic_reissue_end_yes)
                } else {
                    AppCompatResources.getDrawable(view.context, R.drawable.ic_reissue_end_no)
                }
            }
        }
    }
}

@BindingAdapter("serverState")
fun reissueApplyButtonState(
    view: MaterialButton,
    state: ReissueState?
) {
    state?.let {
        when (it.state) {
            "none", "done" -> {
                view.isEnabled = true
                view.backgroundTintList =
                    ColorStateList.valueOf(getColorHelper(view.context, R.color.front_gradient_end))
                view.text = "카드 신청하기"
            }
            "pick_up_requested" -> {
                view.isEnabled = true
                view.backgroundTintList =
                    ColorStateList.valueOf(getColorHelper(view.context, R.color.front_gradient_end))
                view.text = "데스크 카드수령 완료"
            }
            else -> {
                view.isEnabled = false
                view.backgroundTintList =
                    ColorStateList.valueOf(
                        getColorHelper(
                            view.context,
                            R.color.widget_out_state_color
                        )
                    )
                view.text = "카드 신청하기"
            }
        }
    }
}

@BindingAdapter("okButtonState")
fun reissueDialogOkButtonState(
    view: MaterialButton,
    state: ReissueState?
) {
    state?.let {
        if (it.state == "none" || it.state == "done") {
            view.text = view.context.getString(R.string.reissue_dialog_apply_ok_text)
        } else {
            view.text = view.context.getString(R.string.reissue_dialog_done_ok_text)
        }
    }
}

@BindingAdapter("mainWarningState")
fun reissueDialogMainWarningState(
    view: TextView,
    state: ReissueState?,
) {
    state?.let {
        if (it.state == "none" || it.state == "done") {
            view.text = view.context.getString(R.string.reissue_dialog_main_warning_apply_text)
        } else {
            view.text = view.context.getString(R.string.reissue_dialog_main_warning_done_text)
        }
    }
}

@BindingAdapter("subWarningState")
fun reissueDialogSubWarningState(
    view: TextView,
    state: ReissueState?,
) {
    state?.let {
        if (it.state == "none" || it.state == "done") {
            view.text = view.context.getString(R.string.reissue_dialog_sub_warning_apply_text)
        } else {
            view.text = view.context.getString(R.string.reissue_dialog_sub_warning_done_text)
        }
    }
}

@BindingAdapter("loadingState")
fun reissueLoadingConstraint(
    view: ConstraintLayout,
    loadingState: Boolean,
) {
    view.visibility = if (loadingState) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("loadingState")
fun reissueLoadingImageView(
    view: ImageView,
    loadingState: Boolean,
) {
    val drawable = view.drawable as AnimationDrawable
    if (loadingState) {
        drawable.setEnterFadeDuration(300)
        drawable.setExitFadeDuration(300)
        drawable.start()
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.INVISIBLE
        drawable.stop()
    }
}

@BindingAdapter("loadingState")
fun reissueLoadingButton(
    view: MaterialButton,
    loadingState: Boolean,
) {
    if (loadingState) {
        view.isEnabled = false
        view.backgroundTintList =
            ColorStateList.valueOf(getColorHelper(view.context, R.color.widget_out_state_color))
    } else {
        view.isEnabled = true
        view.backgroundTintList =
            ColorStateList.valueOf(getColorHelper(view.context, R.color.front_gradient_end))
    }
}
