package com.hope.firebase.database.aicreator

class Models {

    data class AiProfile(
        val name: String = "",
        val imageUrl: String = "",
        val gender: String = "",
        val type: String = "",              // e.g., Assistant, Mentor, etc.
        val personality: String = "",       // Optional
        val age: Int = 0,                   // Optional
        val description: String = ""        // Optional
    )
}