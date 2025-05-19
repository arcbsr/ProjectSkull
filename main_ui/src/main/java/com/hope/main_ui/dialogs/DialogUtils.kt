package com.hope.main_ui.dialogs

import android.content.Context
import android.view.View
import com.hope.firebase.database.aicreator.Models
import com.lxj.xpopup.XPopup

class DialogUtils {


    companion object {
        fun showBubbleText(context: Context, hintText: String, anchor: View, onClick: () -> Unit) {
            XPopup.Builder(context)
                .atView(anchor) // anchor the bubble to this view
                .hasShadowBg(false)
                .hasStatusBarShadow(false)
                .isRequestFocus(false)
                .offsetY(8)
                .asCustom(CustomBubbleAttachPopupArrow(context, hintText) {
                    onClick()
                })
                .show()
        }

          fun generateAiIntroduction(aiProfile: Models.AiProfile? = null): String {
            return buildString {
                if (aiProfile != null) {
                    append("👋 Hi, I'm *${aiProfile.name}*")
                    if (aiProfile.type.isNotEmpty()) append(", your ${aiProfile.type}")
                    if (aiProfile.gender.isNotEmpty()) append(" (${aiProfile.gender})")
                    if (aiProfile.age > 0) append(", age ${aiProfile.age}")
                    append("!\n")

                    if (aiProfile.personality.isNotEmpty()) {
                        append("🧠 I'm known to be ${aiProfile.personality.lowercase()}.")
                    }

                    if (aiProfile.description.isNotEmpty()) {
                        append("💬 ${aiProfile.description}")
                    } else {
                        append("Let's chat and get to know each other better!")
                    }
                } else {
                    append("🚀 Ready to chat?")
                    append("\n🤖 Please select an AI agent from above to begin your journey.")
                    append("\n🎭 Each one brings a different personality — choose your perfect match!")
                }
            }
        }
    }
}