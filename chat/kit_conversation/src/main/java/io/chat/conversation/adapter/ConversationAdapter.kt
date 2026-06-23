package io.chat.conversation.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import io.chat.conversation.model.UiSession
import io.im.uicommon.adapter.BaseAdapter
import io.im.uicommon.adapter.IViewProviderListener
import io.im.uicommon.adapter.ProviderManager


/**
 * by DAD FZ
 * 2026/6/10
 * desc：
 **/
class ConversationAdapter(listener: IViewProviderListener<UiSession>) : BaseAdapter<UiSession>(
    listener,
    ProviderManager<UiSession>().apply { addProvider(ConversationProvider()) }
) {

    private val mDiffCallback = ConversationDiffCallBack()

    @SuppressLint("NotifyDataSetChanged")
    override fun setDataCollection(data: MutableList<UiSession>) {
        // 当有空布局的时候，需要全部刷新
        if ((mDataList.isEmpty() && data.isNotEmpty())
            || (mDataList.isNotEmpty() && data.isEmpty())
        ) {
            super.setDataCollection(data)
            notifyDataSetChanged()
        } else {
            mDiffCallback.setNewList(data)
            val diffResult = DiffUtil.calculateDiff(mDiffCallback, false)
            super.setDataCollection(data)
            diffResult.dispatchUpdatesTo(
                object : ListUpdateCallback {
                    override fun onInserted(position: Int, count: Int) {
                        notifyItemRangeInserted(headersCount + position, count)
                    }

                    override fun onRemoved(position: Int, count: Int) {
                        notifyItemRangeRemoved(headersCount + position, count)
                    }

                    override fun onMoved(fromPosition: Int, toPosition: Int) {
                        notifyItemMoved(
                            headersCount + fromPosition,
                            headersCount + toPosition
                        )
                    }

                    override fun onChanged(position: Int, count: Int, payload: Any?) {
                        notifyItemRangeChanged(headersCount + position, count, null)
                    }
                })
        }
    }

    inner class ConversationDiffCallBack : DiffUtil.Callback() {
        private var newList: MutableList<UiSession> = mutableListOf()

        override fun getOldListSize(): Int {
            if (mDataList != null) {
                return mDataList.size
            } else {
                return 0;
            }
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mDataList[oldItemPosition].session.sid == newList[newItemPosition].session.sid
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val newItem = newList[newItemPosition]
            if (newItem.isChange) {
                return false
            }
            return true
        }


        fun setNewList(newList: MutableList<UiSession>) {
            this.newList = newList
        }
    }
}