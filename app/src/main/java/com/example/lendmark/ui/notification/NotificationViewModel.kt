package com.example.lendmark.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationViewModel : ViewModel() {

    // 알림 리스트 데이터 (읽기 전용 LiveData)
    private val _notifications = MutableLiveData<List<NotificationItem>>()
    val notifications: LiveData<List<NotificationItem>> get() = _notifications

    // 선택된 알림 (예: 다이얼로그에 표시할 항목)
    private val _selectedNotification = MutableLiveData<NotificationItem?>()
    val selectedNotification: LiveData<NotificationItem?> get() = _selectedNotification

    init {
        // 임시 데이터 (테스트용)
        _notifications.value = listOf(
            NotificationItem(
                id = 1,
                title = "강의실 예약 시작 30분 전입니다",
                detail = "예약 내역: 프론티어관 107호",
                time = "2025-10-23 18:54"
            ),
            NotificationItem(
                id = 2,
                title = "강의실 예약 종료 10분 전입니다",
                detail = "예약 내역: 미래관 205호",
                time = "2025-10-23 20:14"
            )
        )
    }

    // 알림 항목 클릭 시 호출
    fun selectNotification(item: NotificationItem) {
        _selectedNotification.value = item
        markAsRead(item.id)
    }

    //  읽음 처리
    private fun markAsRead(notificationId: Int) {
        _notifications.value = _notifications.value?.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }

    // (옵션) 새로운 알림 추가
    fun addNotification(newItem: NotificationItem) {
        val current = _notifications.value ?: emptyList()
        _notifications.value = listOf(newItem) + current
    }

    // (옵션) 전체 읽음 처리
    fun markAllAsRead() {
        _notifications.value = _notifications.value?.map { it.copy(isRead = true) }
    }
}
