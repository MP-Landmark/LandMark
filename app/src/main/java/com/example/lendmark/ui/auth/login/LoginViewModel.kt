package com.example.lendmark.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lendmark.utils.Event

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _loginResult = MutableLiveData<Event<Boolean>>()
    val loginResult: LiveData<Event<Boolean>> get() = _loginResult

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    // 로그인 함수
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = Event("Please enter both email and password.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                // Firestore에서 유저 정보 확인
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            _loginResult.value = Event(true)
                        } else {
                            _errorMessage.value = Event("User data not found in Firestore.")
                        }
                    }
                    .addOnFailureListener {
                        _errorMessage.value = Event("Failed to fetch user data: ${it.message}")
                    }
            }
            .addOnFailureListener {
                _errorMessage.value = Event("Login failed: ${it.message}")
            }
    }
}
