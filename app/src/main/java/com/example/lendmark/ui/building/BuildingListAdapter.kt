package com.example.lendmark.ui.building

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lendmark.R
import com.example.lendmark.data.model.Building
import com.example.lendmark.databinding.ItemBuildingBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuildingListAdapter(
    private val activity: AppCompatActivity,
    private val buildings: List<Building>,
    private val onClick: (Building) -> Unit
) : RecyclerView.Adapter<BuildingListAdapter.ViewHolder>() {



    inner class ViewHolder(val binding: ItemBuildingBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBuildingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val building = buildings[position]
        with(holder.binding) {
            tvBuildingName.text = building.name
            tvBuildingCode.text = "${building.code}"
            tvBuildingRooms.text = "예약 가능한 강의실 ${building.roomCount}개"

            Glide.with(imgBuilding.context)
                .load(building.imageUrl)
                .centerCrop()
                .into(imgBuilding)

            root.setOnClickListener { onClick(building) }

            // 지도 아이콘 클릭 → 지도 탭으로 (BottomNav nav_map 선택)
            ivLocation.setOnClickListener {
                val bottomNav = activity.findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.selectedItemId = R.id.nav_map
            }
        }
    }

    override fun getItemCount() = buildings.size


}
