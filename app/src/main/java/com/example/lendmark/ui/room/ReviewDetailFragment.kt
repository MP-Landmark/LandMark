package com.example.lendmark.ui.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.lendmark.databinding.FragmentReviewDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt
import com.example.lendmark.data.model.ReviewModel
class ReviewDetailFragment : Fragment() {

    private var _binding: FragmentReviewDetailBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private var buildingId: String? = null
    private var roomId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            buildingId = it.getString("buildingId")
            roomId = it.getString("roomId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadReviews()
    }

    private fun loadReviews() {
        if (buildingId == null || roomId == null) return

        db.collection("reviews")
            .whereEqualTo("buildingId", buildingId)
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { snapshot ->

                val reviews = snapshot.documents.mapNotNull { doc ->
                    ReviewModel.fromDocument(doc)
                }

                updateUI(reviews)
            }
    }

    private fun updateUI(reviews: List<ReviewModel>) {

        if (reviews.isEmpty()) {
            binding.tvNoReview.visibility = View.VISIBLE   // 안내 문구만 표시
            binding.contentContainer.visibility = View.GONE // 시설정보 전체 숨김
            return
        }
        binding.tvNoReview.visibility = View.GONE
        binding.contentContainer.visibility = View.VISIBLE

        // ------------------------------
        // 평균 수용인원
        // ------------------------------
        val avgCapacity = reviews.map { it.capacity }.average().roundToInt()
        binding.tvAvgCapacity.text = "${avgCapacity}명"

        // ------------------------------
        // classType 최다값
        // ------------------------------
        val topClassType = reviews
            .groupingBy { it.classType }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "정보 없음"

        binding.tvMajorClassType.text = topClassType

        // ------------------------------
        // 태그 Count → Chip ex) "빔프로젝터 있음 (8)"
        // ------------------------------
        val tagFrequency = reviews
            .flatMap { it.tags }
            .groupingBy { it }
            .eachCount()
            .toList()
            .sortedByDescending { it.second }

        binding.chipGroupTags.removeAllViews()

        tagFrequency.forEach { (tag, count) ->
            val chip = layoutInflater.inflate(
                com.example.lendmark.R.layout.item_selected_feature_chip,
                binding.chipGroupTags,
                false
            ) as com.google.android.material.chip.Chip

            chip.text = "$tag ($count)"
            binding.chipGroupTags.addView(chip)
        }

        // ------------------------------
        // 사진 Grid (3열)
        // ------------------------------
        val allImages = reviews.flatMap { it.imageUrls }

        val grid = binding.photoGrid
        grid.removeAllViews()

        allImages.forEach { url ->
            val imageView = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    260
                ).apply {
                    width = (resources.displayMetrics.widthPixels / 3) - 40
                    setMargins(6, 6, 6, 6)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            Glide.with(this)
                .load(url)
                .into(imageView)

            grid.addView(imageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
