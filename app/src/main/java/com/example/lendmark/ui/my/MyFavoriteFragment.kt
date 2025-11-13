package com.example.lendmark.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.lendmark.R
import com.example.lendmark.ui.main.MainActivity   // ✅ MainActivity import 추가

class MyFavoriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_favorite, container, false)

        val btnManage = view.findViewById<Button>(R.id.btnManageFavorites)
        btnManage.setOnClickListener {
            // ✅ MainActivity의 함수 호출로 변경
            (activity as? MainActivity)?.openManageFavorites()
        }

        return view
    }
}
