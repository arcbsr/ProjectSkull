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

    companion object {
        private val profiles = listOf(
            AiProfile(
                name = "Aurora",
                imageUrl = "https://play-lh.googleusercontent.com/7Ak4Ye7wNUtheIvSKnVgGL_OIZWjGPZNV6TP_3XLxHC-sDHLSE45aDg41dFNmL5COA",
                gender = "Female",
                type = "Assistant",
                personality = "Friendly and insightful",
                age = 5,
                description = "An advanced AI assistant developed to support users with daily tasks."
            ),
            AiProfile(
                name = "Zeus",
                imageUrl = "https://aiavatar.com/globalImages/landingPage/variants/gaming.webp",
                gender = "Male",
                type = "Mentor",
                personality = "Wise and strategic",
                age = 7,
                description = "A guiding AI mentor designed to provide strategic advice."
            ),
            AiProfile(
                name = "Nova",
                imageUrl = "https://aiavatar.com/globalImages/landingPage/variants/gaming.webp",
                gender = "Non-binary",
                type = "Companion",
                personality = "Curious and empathetic",
                age = 3,
                description = "An empathetic AI companion who loves to learn with you."
            )
        )
        fun getProfiles(): List<AiProfile> {
            return profiles.shuffled().take(3)
        }
    }


}