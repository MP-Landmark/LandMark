package com.example.lendmark.ui.my

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lendmark.databinding.FragmentMyInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyInfoFragment : Fragment() {

    private var _binding: FragmentMyInfoBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val uid = auth.currentUser?.uid ?: return

        // 1. 사용자 기본 정보 불러오기
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (!isAdded) return@addOnSuccessListener

                document?.let {
                    binding.valueName.text = it.getString("name")
                    binding.valueMajor.text = it.getString("department")
                    binding.valueEmail.text = it.getString("email")
                    binding.valuePhone.text = it.getString("phone")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("MyInfoFragment", "Failed to load user info", exception)
            }

        // 2. 총 예약 횟수 불러오기 (취소된 예약 제외)
        db.collection("reservations")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { reservationsSnapshot ->
                if (!isAdded) return@addOnSuccessListener
                
                // 클라이언트에서 "canceled" 상태 필터링
                val validReservations = reservationsSnapshot.documents.filter {
                    it.getString("status") != "canceled"
                }
                
                binding.valueTotalReservation.text = "${validReservations.size}회"
            }
            .addOnFailureListener { exception ->
                if (!isAdded) return@addOnFailureListener
                Log.e("MyInfoFragment", "Error getting reservation count", exception)
                binding.valueTotalReservation.text = "0회"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
