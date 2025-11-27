package com.example.lendmark.ui.chatbot

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lendmark.R
import com.example.lendmark.data.model.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import java.time.LocalDate
import java.time.LocalTime


class ChatBotActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var spinnerTime: Spinner
    private lateinit var spinnerBuilding: Spinner
    private lateinit var btnAskAI: Button
    private lateinit var recyclerChat: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvDateSelector: TextView

    private lateinit var adapter: ChatBotAdapter
    private val messages = mutableListOf<ChatMessage>()

    private val db = FirebaseFirestore.getInstance()
    private val functions = FirebaseFunctions.getInstance()

    private var timeList = mutableListOf<String>()
    private val buildingOptions = mutableListOf<BuildingOption>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupDatePicker()
        loadBuildings()
        setupListeners()

        tvDateSelector.text = LocalDate.now().toString()  // 오늘 날짜 기본
        updateTimeSpinner(tvDateSelector.text.toString()) // 기본 시간 리스트
    }

    private fun initViews() {
        toolbar = findViewById(R.id.chatbotToolbar)
        tvDateSelector = findViewById(R.id.tvDateSelector)
        spinnerTime = findViewById(R.id.spinnerTime)
        spinnerBuilding = findViewById(R.id.spinnerBuilding)
        btnAskAI = findViewById(R.id.btnAskAI)
        recyclerChat = findViewById(R.id.recyclerChat)
        progressBar = findViewById(R.id.progressBarAI)
    }

    /* ---------------- Toolbar ---------------- */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.title = "AI Assistant"
    }

    /* ---------------- RecyclerView ---------------- */
    private fun setupRecyclerView() {
        adapter = ChatBotAdapter(messages)
        recyclerChat.adapter = adapter
        recyclerChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
    }

    /* ---------------- Date Picker ---------------- */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupDatePicker() {

        tvDateSelector.setOnClickListener {

            val today = LocalDate.now()
            val endDate = today.plusDays(28)

            val dialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val date = LocalDate.of(year, month + 1, day)
                    tvDateSelector.text = date.toString()

                    // 날짜 바뀌면 시간 다시 세팅
                    updateTimeSpinner(date.toString())
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            )

            dialog.datePicker.minDate = today.toEpochDay() * 86400000
            dialog.datePicker.maxDate = endDate.toEpochDay() * 86400000

            dialog.show()
        }
    }

    /* ---------------- Time Spinner ---------------- */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTimeSpinner(selectedDate: String) {
        timeList = mutableListOf()

        val today = LocalDate.now().toString()
        val now = LocalTime.now()

        if (selectedDate == today) {

            val currentHour = now.hour

            if (currentHour < 18) {
                timeList.add("지금 바로")
            }

            for (h in 8..17) {
                if (h > currentHour) timeList.add("${h}시")
            }

        } else {
            for (h in 8..17) {
                timeList.add("${h}시")
            }
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            timeList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTime.adapter = adapter
    }

    /* ---------------- Building Spinner ---------------- */
    private fun loadBuildings() {
        db.collection("buildings")
            .orderBy("code")
            .get()
            .addOnSuccessListener { snap ->

                buildingOptions.clear()

                for (doc in snap) {
                    val id = doc.id
                    val name = doc.getString("name")
                        ?: doc.getString("buildingName")
                        ?: id
                    buildingOptions.add(BuildingOption(id, name))
                }

                val names = buildingOptions.map { it.name }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    names
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerBuilding.adapter = adapter
            }
    }

    /* ---------------- Button Listener ---------------- */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        btnAskAI.setOnClickListener {
            askAI()
        }
    }

    /* ---------------- Ask AI ---------------- */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun askAI() {

        val date = tvDateSelector.text.toString()
        val timeLabel = spinnerTime.selectedItem.toString()

        val buildingPos = spinnerBuilding.selectedItemPosition
        if (buildingPos == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "건물을 선택하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val building = buildingOptions[buildingPos]

        addUserMessage("$date $timeLabel 예약 가능한 ${building.name} 강의실 알려줘")

        val hour = convertToHour(timeLabel)
        requestAI(building.id, building.name, date, hour)
    }

    /* "지금 바로" OR "13시" → hour 변환 */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToHour(timeLabel: String): Int {
        return if (timeLabel == "지금 바로") {
            LocalTime.now().hour
        } else {
            timeLabel.replace("시", "").trim().toInt()
        }
    }

    /* ---------------- Chat UI ---------------- */
    private fun addUserMessage(text: String) {
        val msg = ChatMessage(text, true)
        adapter.addMessage(msg)
        recyclerChat.scrollToPosition(adapter.itemCount - 1)
    }

    private fun addAiMessage(text: String) {
        val msg = ChatMessage(text, false)
        adapter.addMessage(msg)
        recyclerChat.scrollToPosition(adapter.itemCount - 1)
    }

    /* ---------------- Cloud Function ---------------- */
    private fun requestAI(buildingId: String, buildingName: String, date: String, hour: Int) {

        progressBar.visibility = View.VISIBLE

        val data = hashMapOf(
            "buildingId" to buildingId,
            "buildingName" to buildingName,
            "date" to date,
            "hour" to hour
        )

        FirebaseFunctions.getInstance("asia-northeast3")
            .getHttpsCallable("chatbotAvailableRoomsV2")
            .call(data)
            .addOnSuccessListener { result ->
                progressBar.visibility = View.GONE
                val map = result.data as? Map<*, *>
                val answer = map?.get("answer") as? String
                    ?: "서버 응답이 올바르지 않습니다."
                addAiMessage(answer)
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                addAiMessage("AI 응답 오류: ${e.message}")
            }
    }

    data class BuildingOption(
        val id: String,
        val name: String
    )
}
