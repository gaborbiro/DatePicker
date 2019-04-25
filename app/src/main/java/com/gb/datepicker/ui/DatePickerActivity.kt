package com.gb.datepicker.ui

import android.os.Bundle
import com.gb.datepicker.R
import com.gb.datepicker.util.prefixZero
import com.gb.datepicker.util.setSubtextWithLinks
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item_day.view.*
import kotlinx.android.synthetic.main.list_item_day_disabled.view.*
import kotlinx.android.synthetic.main.list_item_day_of_week.view.*
import kotlinx.android.synthetic.main.list_item_month.view.*
import kotlinx.android.synthetic.main.list_item_year.view.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class DatePickerActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = ViewPagerAdapter(
            { data, page ->
                updateSelectedDate(data)
                if (page < 2) {
                    view_pager.postDelayed({
                        view_pager.currentItem = page + 1
                    }, 100)
                }
            },
            getYearAdapter(),
            getMonthAdapter(),
            getDayAdapter()
        )
    }

    private fun getYearAdapter() = PageAdapter(
        R.layout.content_years,
        ItemAdapter(
            itemLayoutId = R.layout.list_item_year,
            data = { _: List<Int?> ->
                (Calendar.getInstance().get(Calendar.YEAR) downTo 1970).toList()
            },
            selectable = true
        ) { _, item -> year_value.text = item.toString() }
    )

    private fun getMonthAdapter() = PageAdapter(
        R.layout.content_months,
        ItemAdapter(
            itemLayoutId = R.layout.list_item_month,
            data = { _: List<Int?> -> (1..12).toList() },
            selectable = true
        ) { _, item ->
            month_value.text = Month.of(item)
                .getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    )

    private fun getDayAdapter() = PageAdapter(
        R.layout.content_days,
        getDayOfWeekItemAdapter(),
        getPreDaysItemAdapter(),
        getDaysOfMonthItemAdapter(),
        getPostDaysItemAdapter()
    )

    private fun getDayOfWeekItemAdapter() = ItemAdapter(
        itemLayoutId = R.layout.list_item_day_of_week,
        data = { (1..7).toList() },
        selectable = false
    ) { _: List<Int?>, item ->
        day_of_week_value.text = DayOfWeek.of(item).getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    private fun getPreDaysItemAdapter() = ItemAdapter(
        itemLayoutId = R.layout.list_item_day_disabled,
        data = { data: List<Int?> ->
            val firstDayOfMonth = LocalDate.of(data[0]!!, data[1]!!, 1)
            val preDayCount = firstDayOfMonth.dayOfWeek.value - 1
            (preDayCount downTo 1).toList()
        },
        selectable = false
    ) { data, item ->
        val preDay = LocalDate.of(data[0]!!, data[1]!!, 1).minusDays(item.toLong())
        disabled_day_value.text = preDay.dayOfMonth.toString()
    }

    private fun getDaysOfMonthItemAdapter() = ItemAdapter(
        itemLayoutId = R.layout.list_item_day,
        data = { data: List<Int?> ->
            val maxDays = YearMonth.of(data[0]!!, Month.of(data[1]!!)).lengthOfMonth()
            (1..maxDays).toList()
        },
        selectable = true
    ) { _, item -> day_value.text = item.toString() }

    private fun getPostDaysItemAdapter() = ItemAdapter(
        itemLayoutId = R.layout.list_item_day_disabled,
        data = { data: List<Int?> ->
            val maxDays = YearMonth.of(data[0]!!, Month.of(data[1]!!)).lengthOfMonth()
            val postDayCount = 7 - LocalDate.of(data[0]!!, data[1]!!, maxDays).dayOfWeek.value
            (1..postDayCount).toList()
        },
        selectable = false
    ) { _, item -> disabled_day_value.text = item.toString() }

    private fun updateSelectedDate(data: List<Int?>) {
        if (data[0] != null) {
            if (data[1] != null) {
                val monthStr = Month.of(data[1]!!)
                    .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                if (data[2] != null) {
                    result_label.setSubtextWithLinks(
                        text = "${data[0]}-$monthStr-${data[2]!!.prefixZero()}",
                        linkParts = arrayOf(
                            data[0].toString(),
                            monthStr,
                            data[2]!!.prefixZero()
                        ),
                        runOnClicks = arrayOf(
                            { view_pager.currentItem = 0 },
                            { view_pager.currentItem = 1 },
                            { view_pager.currentItem = 2 }
                        )
                    )
                } else {
                    result_label.setSubtextWithLinks(
                        text = "${data[0]}-$monthStr",
                        linkParts = arrayOf(data[0].toString(), monthStr),
                        runOnClicks = arrayOf(
                            { view_pager.currentItem = 0 },
                            { view_pager.currentItem = 1 }
                        )
                    )
                }
            } else {
                result_label.text = "${data[0]}"
            }
        } else {
            result_label.text = ""
        }
    }
}