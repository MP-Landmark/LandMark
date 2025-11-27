package com.example.lendmark.data.model
data class ReviewModel(
    val capacity: Int = 0,
    val classType: String = "",
    val tags: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList()
) {
    companion object {
        fun fromDocument(doc: com.google.firebase.firestore.DocumentSnapshot): ReviewModel {
            return ReviewModel(
                capacity = doc.getLong("capacity")?.toInt() ?: 0,
                classType = doc.getString("classType") ?: "",
                tags = doc.get("tags") as? List<String> ?: emptyList(),
                imageUrls = doc.get("imageUrls") as? List<String> ?: emptyList()
            )
        }
    }
}
