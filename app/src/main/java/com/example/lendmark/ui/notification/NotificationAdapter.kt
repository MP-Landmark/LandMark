package com.example.lendmark.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lendmark.databinding.ItemNotificationBinding

// 알림 데이터 모델 (id, 제목, 내용, 시간, 읽음여부)
data class NotificationItem(
    val id: Int,
    val title: String,
    val detail: String,
    val time: String = "",
    var isRead: Boolean = false
)

class NotificationAdapter(
    private var items: List<NotificationItem>,
    private val onClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationItem) {
            binding.tvTitle.text = item.title
            binding.tvDetail.text = item.detail

            // 클릭 이벤트 연결
            binding.root.setOnClickListener { onClick(item) }

            // 읽음 여부에 따라 색상 살짝 바꾸기 (선택 사항)
            if (item.isRead) {
                binding.tvTitle.alpha = 0.6f
                binding.tvDetail.alpha = 0.6f
            } else {
                binding.tvTitle.alpha = 1.0f
                binding.tvDetail.alpha = 1.0f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    // ViewModel에서 새로운 리스트가 들어왔을 때 업데이트용
    fun updateList(newItems: List<NotificationItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
