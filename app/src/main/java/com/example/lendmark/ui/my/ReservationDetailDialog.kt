package com.example.lendmark.ui.my

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.lendmark.R
import com.example.lendmark.databinding.DialogReservationDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class ReservationDetailDialogFS(
    private val reservation: ReservationFS,
    private val onCancelClick: (String) -> Unit,
    private val onRegisterClick: (String) -> Unit
) : DialogFragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogReservationDetailBinding.inflate(LayoutInflater.from(context))

        // ──────────────────────────────────────
        // 1) Firestore에서 building name 불러오기
        // ──────────────────────────────────────
        db.collection("buildings").document(reservation.buildingId)
            .get()
            .addOnSuccessListener { doc ->

                val buildingName = doc.getString("name") ?: "Unknown Building"

                // "14. Frontier Hall"
                binding.tvBuildingName.text = "${reservation.buildingId}. $buildingName"
            }
            .addOnFailureListener {
                binding.tvBuildingName.text = reservation.buildingId // fallback
            }

        // 강의실 번호
        binding.tvRoomName.text = "No. ${reservation.roomId}"

        // 날짜/시간 정보
        binding.tvDate.text = reservation.date
        binding.tvTime.text =
            "${periodToTime(reservation.periodStart)} - ${periodToTime(reservation.periodEnd)}"

        binding.tvAttendees.text = "${reservation.attendees} people"
        binding.tvPurpose.text = reservation.purpose

        // 닫기 버튼
        binding.btnClose.setOnClickListener { dismiss() }

        // ──────────────────────────────────────
        // 2) 상태별 버튼 UI
        // ──────────────────────────────────────
        when (reservation.status) {

            "approved" -> {
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnRegisterInfo.visibility = View.GONE

                binding.btnCancel.text = "Cancel Reservation"
                binding.btnCancel.isEnabled = true
                binding.btnCancel.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.btnCancel.strokeColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.red)

                binding.btnCancel.setOnClickListener {
                    onCancelClick(reservation.id)
                    dismiss()
                }
            }

            "finished" -> {
                binding.btnCancel.visibility = View.GONE
                binding.btnRegisterInfo.visibility = View.VISIBLE

                binding.btnRegisterInfo.text = "Register Classroom Info"
                binding.btnRegisterInfo.isEnabled = true
                binding.btnRegisterInfo.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                binding.btnRegisterInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                binding.btnRegisterInfo.setOnClickListener {
                    onRegisterClick(reservation.id)
                    dismiss()
                }
            }

            "canceled" -> {
                binding.btnCancel.visibility = View.GONE
                binding.btnRegisterInfo.visibility = View.GONE
            }
        }

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun periodToTime(period: Int): String {
        val hour = 8 + period
        return String.format("%02d:00", hour)
    }
}
