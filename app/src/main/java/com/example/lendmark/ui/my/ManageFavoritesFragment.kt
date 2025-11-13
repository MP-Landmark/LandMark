package com.example.lendmark.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lendmark.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class ManageFavoritesFragment : Fragment() {

    private lateinit var favoriteContainer: LinearLayout
    private lateinit var allContainer: LinearLayout
    private lateinit var btnAddBuilding: MaterialButton

    private val allBuildings = mutableListOf<Building>()
    private val favoriteBuildings = mutableListOf<Building>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_manage_favorites, container, false)

        // 뷰 바인딩
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbarFavorites)
        favoriteContainer = view.findViewById(R.id.layoutFavoriteBuildings)
        allContainer = view.findViewById(R.id.layoutAllBuildings)
        btnAddBuilding = view.findViewById(R.id.btnAddBuilding)

        // 툴바 뒤로가기
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupMockData()
        renderFavorites()
        renderAllBuildings()

        btnAddBuilding.setOnClickListener {
            showAddBuildingDialog()
        }

        return view
    }

    private fun setupMockData() {
        allBuildings.clear()
        allBuildings.addAll(
            listOf(
                Building("다산관", 15),
                Building("창학관", 12),
                Building("미래관", 10),
                Building("예지관", 8),
                Building("상상관", 14),
                Building("인문관", 9),
                Building("제1학생회관", 6),
                Building("제2학생회관", 5)
            )
        )

        favoriteBuildings.clear()
        favoriteBuildings.addAll(
            listOf(
                allBuildings[0], // 다산관
                allBuildings[1]  // 창학관
            )
        )
    }

    private fun renderFavorites() {
        favoriteContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        if (favoriteBuildings.isEmpty()) {
            val tv = TextView(requireContext()).apply {
                text = "즐겨찾는 건물을 추가해 주세요."
                setTextColor(resources.getColor(R.color.text_secondary, null))
                textSize = 14f
            }
            favoriteContainer.addView(tv)
            return
        }

        favoriteBuildings.forEach { building ->
            val itemView = inflater.inflate(
                R.layout.item_favorite_building,
                favoriteContainer,
                false
            )

            val tvName = itemView.findViewById<TextView>(R.id.tvFavoriteName)
            val tvRooms = itemView.findViewById<TextView>(R.id.tvFavoriteRooms)

            tvName.text = building.name
            tvRooms.text = "${building.roomCount}개 강의실"

            favoriteContainer.addView(itemView)
        }
    }

    private fun renderAllBuildings() {
        allContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        allBuildings.forEach { building ->
            val itemView = inflater.inflate(
                R.layout.item_all_building,
                allContainer,
                false
            )

            val tvName = itemView.findViewById<TextView>(R.id.tvAllName)
            val tvRooms = itemView.findViewById<TextView>(R.id.tvAllRooms)
            val tvStar = itemView.findViewById<TextView>(R.id.tvAllStar)

            tvName.text = building.name
            tvRooms.text = "${building.roomCount}개 강의실"

            // 즐겨찾기 여부에 따라 별 표시
            if (favoriteBuildings.any { it.name == building.name }) {
                tvStar.visibility = View.VISIBLE
            } else {
                tvStar.visibility = View.INVISIBLE
            }

            allContainer.addView(itemView)
        }
    }

    private fun showAddBuildingDialog() {
        val dialog = AddBuildingDialogFragment()

        // 현재 즐겨찾기 아닌 건물만 전달
        val candidates = allBuildings.filter { b ->
            favoriteBuildings.none { it.name == b.name }
        }

        dialog.setCandidateBuildings(candidates)
        dialog.onBuildingSelected = { selected ->
            if (favoriteBuildings.none { it.name == selected.name }) {
                favoriteBuildings.add(selected)
                renderFavorites()
                renderAllBuildings()
            }
        }

        dialog.show(parentFragmentManager, "AddBuildingDialog")
    }
}
