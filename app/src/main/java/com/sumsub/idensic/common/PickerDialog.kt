package com.sumsub.idensic.common

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.sumsub.sns.R
import com.sumsub.sns.core.widget.SNSToolbarView
import kotlinx.parcelize.Parcelize
import java.text.Collator
import java.util.Collections
import java.util.Locale

internal class PickerDialog : BottomSheetDialogFragment() {

    interface PickerCallBack {
        fun onItemSelected(item: Item)
        fun onDismiss() {
            // empty
        }

        fun onDialogClose(): Unit = Unit
        fun onCancel(): Unit = Unit
    }

    @Parcelize
    data class Item(val id: String, val title: String) : Parcelable

    var pickerCallBack: PickerCallBack? = null
    private val sortAlphabetically: Boolean
        get() = requireArguments().getBoolean(EXTRA_SORT, true)
    private val showSearch: Boolean
        get() = requireArguments().getBoolean(EXTRA_SHOW_SEARCH, true)

    private val itemLayoutId: Int
        get() = requireArguments().getInt(EXTRA_ITEM_LAYOUT_ID)

    private val internalPickerCallback: PickerCallBack = object : PickerCallBack {
        override fun onItemSelected(item: Item) {
            val requestKey = arguments?.getString(EXTRA_REQUEST_KEY)
            val resultKey = arguments?.getString(EXTRA_RESULT_KEY)
            if (requestKey != null && resultKey != null) {
                setFragmentResult(requestKey, Bundle().apply { putParcelable(resultKey, item) })
            } else {
                pickerCallBack?.onItemSelected(item)
            }
            pickerCallBack?.onDialogClose()
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            if (showSearch) {
                setupFullHeight(bottomSheetDialog)
            } else {
                val bottomSheet = bottomSheetDialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet
                ) as FrameLayout
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.isFitToContents = true
            }
        }
        return dialog
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        pickerCallBack?.onCancel()
    }

    override fun onDismiss(dialog: DialogInterface) {
        pickerCallBack?.onDismiss()
        super.onDismiss(dialog)
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            if (showSearch) R.layout.sns_picker_dialog else R.layout.sns_picker_dialog_no_search,
            container,
            false
        )
    }

    @Suppress("LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = view.findViewById<SNSToolbarView?>(R.id.sns_toolbar)
        toolBar?.setCloseButtonDrawable(
            ResourcesCompat.getDrawable(
                requireContext().resources,
                R.drawable.sns_ic_close,
                requireContext().theme
            )
        )
        toolBar?.setOnCloseButtonClickListener {
            pickerCallBack?.onDialogClose()
            dismiss()
        }

        val searchEditLayout: TextInputLayout? = view.findViewById(R.id.sns_editor_layout)
        searchEditLayout?.editText?.hint = "search..."
        searchEditLayout?.startIconDrawable = ResourcesCompat.getDrawable(
            requireContext().resources,
            R.drawable.sns_ic_search,
            requireContext().theme,
        )

        @Suppress("DEPRECATION")
        val items = arguments?.getParcelableArray(EXTRA_ITEMS)
            ?.filterIsInstance<Item>()
            ?.toTypedArray()

        val adapter = ItemAdapter(
            items = items,
            collator = Collator.getInstance(getDeviceLocale()),
            itemViewHolderFactory = { parentView ->
                createItemViewHolder(parentView)
            }
        )
        searchEditLayout?.editText?.doOnTextChanged { _, _, _, _ ->
            adapter.filter.filter(searchEditLayout.editText?.text)
        }

        val recyclerView: RecyclerView? = view.findViewById(R.id.sns_list)
        recyclerView?.layoutManager
        recyclerView?.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView?.adapter = adapter
        if (!showSearch) {
            val listLayoutParams = recyclerView?.layoutParams
            if (listLayoutParams is ViewGroup.MarginLayoutParams) {
                listLayoutParams.topMargin = 0
                recyclerView.layoutParams = listLayoutParams
            }
        }

        val color = ContextCompat.getColor(requireContext(), R.color.sns_listSeparator)
        val height = 1.px.toFloat()
        val marginHorizontal = resources.getDimensionPixelSize(R.dimen.sns_margin_medium).toFloat()
        recyclerView?.addItemDecoration(
            DividersItemDecoration(
                marginLeft = marginHorizontal,
                marginRight = marginHorizontal,
                separatorHeight = height,
                color = color,
            )
        )
    }

    private inner class ItemAdapter(
        private val items: Array<Item>?,
        private val collator: Collator,
        private val itemViewHolderFactory: ((parentView: ViewGroup) -> PickerItemViewHolder)
    ) : RecyclerView.Adapter<PickerItemViewHolder>(), Filterable {

        private val itemComparator: java.util.Comparator<Item> =
            Comparator { o1, o2 -> collator.compare(o1.title, o2.title) }

        private val filteredItems = mutableListOf<Item>().apply {
            items?.let { addAll(it) }
            if (sortAlphabetically) {
                Collections.sort(this, itemComparator)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerItemViewHolder {
            return itemViewHolderFactory.invoke(parent)
        }

        override fun onBindViewHolder(holder: PickerItemViewHolder, position: Int) {
            val item = filteredItems[position]
            holder.bind(item)
        }

        override fun getItemCount() = filteredItems.size

        override fun getFilter(): Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults().apply {

                    val eligibleItems =
                        items?.filter { it.isEligibleForQuery(constraint?.trim()) }?.toMutableList() ?: mutableListOf()
                    if (sortAlphabetically) {
                        Collections.sort(eligibleItems, itemComparator)
                    }

                    values = eligibleItems
                    count = eligibleItems.size
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                (results?.values as? List<Item>)?.let {
                    val updates = DiffUtil.calculateDiff(DiffCallBack(filteredItems, it))
                    filteredItems.clear()
                    filteredItems.addAll(it)
                    updates.dispatchUpdatesTo(this@ItemAdapter)
                }
            }

        }
    }

    private fun createItemViewHolder(parentView: ViewGroup): PickerItemViewHolder {
        val itemView = LayoutInflater.from(parentView.context)
            .inflate(itemLayoutId, parentView, false)
        return PickerItemViewHolder(itemView)
    }

    private inner class PickerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Item) {
            bindItemViewHolder(this, item)
        }
    }

    @CallSuper
    private fun bindItemViewHolder(
        holder: PickerItemViewHolder,
        item: Item
    ) {
        val itemView = holder.itemView
        val textView: TextView = itemView.findViewById(android.R.id.text1)
        textView.text = item.title
        itemView.setOnClickListener {
            internalPickerCallback.onItemSelected(item)
        }
    }

    private class DiffCallBack(
        private val oldList: List<Item>,
        private val newList: List<Item>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    class DividersItemDecoration(
        private val marginLeft: Float = 0f,
        private val marginRight: Float = 0f,
        private val separatorHeight: Float = 1.px.toFloat(),
        private val color: Int
    ) : RecyclerView.ItemDecoration() {
        private val paint = Paint().apply {
            color = this@DividersItemDecoration.color
            strokeWidth = separatorHeight
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val childCount = parent.childCount
            parent.children.forEachIndexed { index, view ->
                if (index == childCount - 1) return@forEachIndexed
                val height = view.bottom.toFloat()
                val width = view.right.toFloat()
                c.drawLine(marginLeft, height, width - marginRight, height, paint)
            }
        }
    }

    companion object {

        const val TAG = "SNSPickerDialog"

        internal const val EXTRA_ITEMS = "extra_items"
        internal const val EXTRA_ITEM_LAYOUT_ID = "extra_item_layout_id"

        internal const val EXTRA_SORT = "extra_sort"
        internal const val EXTRA_SHOW_SEARCH = "extra_show_search"
        internal const val EXTRA_REQUEST_KEY = "extra_request_key"
        internal const val EXTRA_RESULT_KEY = "EXTRA_RESULT_KEY"

        @Suppress("LongParameterList")
        fun newInstance(
            items: Array<Item>,
            @LayoutRes itemLayoutId: Int,
            sort: Boolean = true,
            showSearch: Boolean = true,
            requestKey: String? = null,
            resultKey: String? = null
        ): PickerDialog = PickerDialog().apply {
            arguments = Bundle().apply {
                putParcelableArray(EXTRA_ITEMS, items)
                putInt(EXTRA_ITEM_LAYOUT_ID, itemLayoutId)
                putBoolean(EXTRA_SORT, sort)
                putBoolean(EXTRA_SHOW_SEARCH, showSearch)
                requestKey?.let { putString(EXTRA_REQUEST_KEY, it) }
                resultKey?.let { putString(EXTRA_RESULT_KEY, it) }
            }
        }
    }

    /**
     * Checks that 'query' string is present in item's title
     */
    internal fun Item.isEligibleForQuery(query: CharSequence?): Boolean {
        if (query.isNullOrEmpty()) {
            return true
        }
        return title.contains(query, ignoreCase = true)
    }
}

private fun getDeviceLocale(): Locale =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        LocaleList.getDefault().get(0)
    } else {
        Locale.getDefault()
    }

private val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
