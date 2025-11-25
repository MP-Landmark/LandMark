package com.example.lendmark.ui.my

import android.content.Intent
import android.os.Bundle
import com.example.lendmark.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.lendmark.databinding.FragmentMyPageBinding
import com.example.lendmark.ui.auth.AuthActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshProfileCard()

        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(com.example.lendmark.R.id.my_toggle_group)
        val btnInfo = view.findViewById<MaterialButton>(com.example.lendmark.R.id.btn_my_info)

        binding.btnEditProfile.setOnClickListener {
            loadMajorsAndShowDialog()
        }

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(com.example.lendmark.R.id.my_content_container, MyInfoFragment())
                .commit()
            toggleGroup.check(btnInfo.id)
        }

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val fragment = when (checkedId) {
                com.example.lendmark.R.id.btn_my_info -> MyInfoFragment()
                com.example.lendmark.R.id.btn_my_reservation -> MyReservationFragment()
                com.example.lendmark.R.id.btn_my_favorite -> MyFavoriteFragment()
                else -> null
            }

            fragment?.let {
                childFragmentManager.beginTransaction()
                    .replace(com.example.lendmark.R.id.my_content_container, it)
                    .commit()
            }
        }

        binding.tvLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

    }

    private fun loadMajorsAndShowDialog() {
        db.collection("timetables")
            .document("2025-fall")
            .collection("departments")
            .get()
            .addOnSuccessListener { result ->
                val majorsList = result.documents.mapNotNull { it.getString("department") }

                if (isAdded) {
                    EditProfileDialog(majorsList) {
                        refreshProfileCard()
                        refreshMyInfo()
                    }.show(childFragmentManager, "EditProfileDialog")
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load majors list.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun refreshMyInfo() {
        childFragmentManager.beginTransaction()
            .replace(com.example.lendmark.R.id.my_content_container, MyInfoFragment())
            .commit()
    }

    private fun refreshProfileCard() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!isAdded || _binding == null) return@addOnSuccessListener

                binding.tvUserName.text = doc.getString("name") ?: ""
                binding.tvUserMajor.text = doc.getString("department") ?: ""
                binding.tvUserEmail.text = doc.getString("email") ?: ""

                val imageUrl = doc.getString("profileImageUrl")

                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.ivUserProfile)
                } else {
                    binding.ivUserProfile.setImageResource(R.drawable.ic_default_profile)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
