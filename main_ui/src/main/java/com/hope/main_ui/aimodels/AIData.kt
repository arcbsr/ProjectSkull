package com.hope.main_ui.aimodels

class AIData {

    data class AIName(val name: String, val description: String)

    companion object {
        fun getRandomThreeAINames(): List<AIName> {
            val aiNames = listOf(
                AIName("Nexa", "Modern, techy, gender-neutral"),
                AIName("Vira", "Human-like with a virtual assistant vibe"),
                AIName("Orbi", "Short, catchy, futuristic"),
                AIName("Eloi", "Sci-fi inspired, soft and intelligent"),
                AIName("Auri", "Elegant and adaptable"),
                AIName("Lyra", "Star-inspired, sleek and smart"),
                AIName("Zeno", "Logic and futuristic AI"),
                AIName("Mira", "Simple, clear, and friendly"),
                AIName("Sora", "Light, gentle, and universal"),
                AIName("Nova", "Powerful and bright")
            )
            return aiNames.shuffled().take(3)
        }
    }

}