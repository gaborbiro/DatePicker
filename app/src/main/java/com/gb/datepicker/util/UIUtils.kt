package com.gb.datepicker.util

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes

inline fun <reified T : View> ViewGroup.inflate(@LayoutRes resource: Int, attachToRoot: Boolean = false): T =
    android.view.LayoutInflater.from(this.context).inflate(resource, this, attachToRoot) as T

@Suppress("UNCHECKED_CAST")
inline fun <reified T : View> ViewGroup.add(@LayoutRes resource: Int, params: ViewGroup.LayoutParams? = null): T =
    (inflate(resource) as T).also { view ->
        params?.let { addView(view, it) } ?: run { addView(view) }
    }

fun Int.prefixZero(): String {
    if (this > 9) {
        return this.toString()
    }
    return "0$this"
}

fun TextView.setSubtextWithLinks(text: String, linkParts: Array<String>, runOnClicks: Array<() -> Unit>) {
    this.text = text
    val spannable = SpannableString(this.text)
    var endIndex: Int = 0

    for (i in linkParts.indices) {
        endIndex = applyLink(this.text.toString(), spannable, linkParts[i], runOnClicks[i], endIndex)
    }

    this.setText(spannable, TextView.BufferType.SPANNABLE)
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setTextWithColor(text: String, ranges: List<Pair<Int, Int>>, @ColorInt color: Int) {
    val spannable = SpannableString(text)
    for (range in ranges) {
        spannable.setSpan(ForegroundColorSpan(color), range.first, range.second, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    this.setText(spannable, TextView.BufferType.SPANNABLE)
}

private fun applyLink(text: String, spannable: Spannable, linkPart: String, runOnClick: () -> Unit, from: Int): Int {
    val startIndex = text.indexOf(linkPart, startIndex = from)
    if (startIndex < 0) {
        throw IllegalArgumentException("linkPart must be included in text")
    }
    spannable.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            try {
                runOnClick()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }, startIndex, startIndex + linkPart.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    return startIndex + linkPart.length
}