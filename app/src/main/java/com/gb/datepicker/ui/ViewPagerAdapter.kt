package com.gb.datepicker.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.LayoutRes
import androidx.viewpager.widget.PagerAdapter
import com.gb.datepicker.R
import com.gb.datepicker.util.add
import kotlinx.android.synthetic.main.page_base.view.*

class ViewPagerAdapter<T>(
    private val onItemSelected: (data: List<T?>, page: Int) -> Unit,
    private vararg val adapters: PageAdapter<T>
) : PagerAdapter() {

    private val activeAdapter: MutableList<PageAdapter<T>> = mutableListOf(adapters[0])

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val view = collection.add<View>(R.layout.page_base)
        activeAdapter[position].setupPage(view.scroll_view, getData()) {
            if (position + 1 < adapters.size) {
                if (!activeAdapter.contains(adapters[position + 1])) {
                    activeAdapter.add(adapters[position + 1])
                    notifyDataSetChanged()
                }
            }
            onItemSelected.invoke(getData(), position)
        }
        return view
    }

    private fun getData(): List<T?> {
        return adapters.map { it.selectedItem }
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount() = activeAdapter.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}

class PageAdapter<T>(
    @LayoutRes private val layoutId: Int,
    private vararg val itemAdapters: ItemAdapter<T>
) {

    var selectedItem: T? = null
        private set
    var selectedView: View? = null
        private set

    fun setupPage(parent: ScrollView, data: List<T?>, onSelected: () -> Unit) {
        parent.add<ViewGroup>(layoutId).apply {
            removeAllViews()
            for (itemAdapter in itemAdapters) {
                for (item in itemAdapter.data.invoke(data)) {
                    add<View>(itemAdapter.itemLayoutId).apply {
                        itemAdapter.viewAdapter.invoke(this, data, item)
                        if (itemAdapter.selectable && item == selectedItem) {
                            selectedView = this
                            isSelected = true
                        }
                        setOnClickListener {
                            if (itemAdapter.selectable) {
                                selectedItem = item
                                selectedView?.isSelected = false
                                selectedView = this
                                isSelected = true
                            }
                            onSelected.invoke()
                        }
                    }
                }
            }
        }
    }
}

class ItemAdapter<T>(
    @LayoutRes val itemLayoutId: Int,
    val data: (List<T?>) -> List<T>,
    val selectable: Boolean = false,
    val viewAdapter: View.(data: List<T?>, item: T) -> Unit
)