package com.example.lendmark.ui.notification

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.lendmark.databinding.DialogNotificationDetailBinding

class NotificationDetailDialog(
    private val item: NotificationItem
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogNotificationDetailBinding.inflate(LayoutInflater.from(context))

        // 제목, 내용 표시
        binding.tvTitle.text = item.title
        binding.tvDetail.text = item.detail

        // “예약 상세로 가기” 버튼 (아직 기능 미구현)
        binding.btnGoReservation.setOnClickListener {
            // TODO: 예약 상세 페이지로 이동 (추후 구현)
            dismiss()
        }

        // 확인 버튼
        binding.btnConfirm.setOnClickListener {
            dismiss()
        }

        // Dialog 스타일 적용
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }
}
